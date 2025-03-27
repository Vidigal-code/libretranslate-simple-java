package com.vidigal.code.libretranslate.http;

import com.vidigal.code.libretranslate.config.LibreTranslateConfig;
import com.vidigal.code.libretranslate.exception.TranslationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class manages connection setup, request formatting, and response handling,
 * including specific logic for translation responses and rate limiting scenarios.
 */
public class HttpRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestHandler.class);

    private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final LibreTranslateConfig config;

    /**
     * Creates a new HttpRequestHandler with the given configuration.
     *
     * @param config The LibreTranslate configuration
     */
    public HttpRequestHandler(LibreTranslateConfig config) {
        this.config = config;
    }

    /**
     * Sends an HTTP request with retry logic based on maxRetries.
     *
     * @param url    The URL to send the request to
     * @param method The HTTP method to use (e.g., "GET", "POST")
     * @param params The parameters to include in the request
     * @return An HttpResponse object containing the response data
     * @throws TranslationException if all retries fail
     */
    public HttpResponse sendHttpRequest(String url, String method, Map<String, String> params) throws TranslationException {
        int maxRetries = config.getMaxRetries();
        int attempt = 0;
        while (true) {
            HttpURLConnection connection = null;
            long startTime = System.currentTimeMillis();

            try {
                connection = setupConnection(url, method);

                if ("POST".equals(method) && params != null && !params.isEmpty()) {
                    writeRequestBody(connection, params);
                }

                return readResponse(connection, startTime);
            } catch (IOException e) {
                attempt++;
                if (attempt > maxRetries) {
                    throw new TranslationException("Failed to send HTTP request after " + maxRetries + " attempts: " + e.getMessage(), e);
                }
                LOGGER.warn("Attempt {} failed, retrying... Error: {}", attempt, e.getMessage());
                sleepBeforeRetry(attempt);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    /**
     * Sets up the HTTP connection with appropriate headers and timeouts.
     *
     * @param url    The URL to connect to
     * @param method The HTTP method to use
     * @return Configured HttpURLConnection
     * @throws IOException If an I/O error occurs
     */
    private HttpURLConnection setupConnection(String url, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput("POST".equals(method));
        connection.setConnectTimeout(config.getTimeout());
        connection.setReadTimeout(config.getReadTimeout());

        // Set common headers
        connection.setRequestProperty("Content-Type", DEFAULT_CONTENT_TYPE);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "LibreTranslateJava/1.0");

        return connection;
    }

    /**
     * Writes the request parameters to the connection output stream.
     *
     * @param connection The connection to write to
     * @param params     The parameters to write
     * @throws IOException If an I/O error occurs
     */
    private void writeRequestBody(HttpURLConnection connection, Map<String, String> params) throws IOException {
        String formData = encodeFormData(params);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = formData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }

    /**
     * Encodes parameters as form data.
     *
     * @param params The parameters to encode
     * @return Encoded form data string
     * @throws IOException If encoding fails
     */
    private String encodeFormData(Map<String, String> params) throws IOException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return result.toString();
    }

    /**
     * Reads the HTTP response into an HttpResponse object.
     *
     * @param connection The connection to read from
     * @param startTime  The start time of the request (for timing)
     * @return An HttpResponse object
     * @throws IOException If an I/O error occurs
     */
    private HttpResponse readResponse(HttpURLConnection connection, long startTime) throws IOException {
        int responseCode = connection.getResponseCode();
        Map<String, String> headers = extractHeaders(connection);
        String responseBody = readResponseBody(connection, responseCode);
        long responseTime = System.currentTimeMillis() - startTime;

        return new HttpResponse(responseCode, headers, responseBody, responseTime);
    }

    /**
     * Extracts headers from the HTTP connection.
     *
     * @param connection The connection to extract headers from
     * @return A map of header names to values
     */
    private Map<String, String> extractHeaders(HttpURLConnection connection) {
        Map<String, String> headers = new HashMap<>();

        for (int i = 0; ; i++) {
            String headerName = connection.getHeaderFieldKey(i);
            String headerValue = connection.getHeaderField(i);

            if (headerName == null && headerValue == null) {
                break;
            }

            if (headerName != null) {
                headers.put(headerName, headerValue);
            }
        }

        return headers;
    }

    /**
     * Reads the response body from the connection.
     *
     * @param connection   The connection to read from
     * @param responseCode The HTTP response code
     * @return The response body as a string
     * @throws IOException If an I/O error occurs
     */
    private String readResponseBody(HttpURLConnection connection, int responseCode) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        responseCode >= 400
                                ? connection.getErrorStream()
                                : connection.getInputStream(),
                        StandardCharsets.UTF_8)
        )) {
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        }
    }

    /**
     * Sleeps before retrying, with exponential backoff.
     *
     * @param attempt The current retry attempt number
     */
    private void sleepBeforeRetry(int attempt) {
        long sleepTime = (long) Math.pow(2, attempt) * 1000; // Exponential backoff
        LOGGER.debug("Sleeping for {} ms before retry", sleepTime);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Thread interrupted during sleep before retry");
        }
    }
}