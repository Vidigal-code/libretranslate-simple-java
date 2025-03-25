package com.vidigal.code.libretranslate.response;

import com.vidigal.code.libretranslate.config.LibreTranslateConfig;
import com.vidigal.code.libretranslate.exception.TranslationException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Utility class for handling HTTP requests in the LibreTranslate client.
 * Manages connection, request building, and response handling.
 */
public class RequestHelper {
    private static final Logger LOGGER = Logger.getLogger(RequestHelper.class.getName());

    private final LibreTranslateConfig config;

    /**
     * Constructs a RequestHelper with the given configuration.
     *
     * @param config Configuration for HTTP requests
     */
    public RequestHelper(LibreTranslateConfig config) {
        this.config = config;
    }

    /**
     * Sends an HTTP request to the specified API URL using the given method and parameters.
     *
     * @param apiUrl The target API URL.
     * @param method The HTTP method (e.g., "GET", "POST").
     * @param params The request parameters as a map.
     * @return An HttpResponse object containing the response code, body, and headers.
     * @throws IOException If an I/O error occurs while sending the request.
     */
    public HttpResponse sendHttpRequest(String apiUrl, String method, Map<String, String> params) throws IOException {
        HttpURLConnection connection = null;
        int maxRetries = config.getMaxRetries();
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Accept", "application/json");
                connection.setConnectTimeout(config.getTimeout());
                connection.setReadTimeout(config.getReadTimeout());

                if ("POST".equals(method)) {
                    connection.setDoOutput(true);
                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(buildRequestBody(params).getBytes(StandardCharsets.UTF_8));
                    }
                }

                int responseCode = connection.getResponseCode();
                String responseBody = readResponseBody(connection, responseCode);

                if (responseCode >= 400) {
                    LOGGER.warning("HTTP request failed with code " + responseCode + ": " + responseBody);
                }

                return new HttpResponse(responseCode, responseBody, connection.getHeaderFields());
            } catch (IOException e) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw e;
                }
                LOGGER.warning("Retrying request... Attempt " + attempt + " of " + maxRetries);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        throw new TranslationException("Request failed after " + maxRetries + " attempts.");
    }

    /**
     * Reads the response body from the connection.
     *
     * @param connection   The HTTP connection
     * @param responseCode The HTTP response code
     * @return The response body as a string
     * @throws IOException if reading the response fails
     */
    private String readResponseBody(HttpURLConnection connection, int responseCode) throws IOException {
        StringBuilder responseBody = new StringBuilder();
        try (InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                ? connection.getInputStream()
                : connection.getErrorStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                responseBody.append(line.trim());
            }
        }
        return responseBody.toString();
    }

    /**
     * Builds the request body from parameters.
     *
     * @param params The parameters map
     * @return The encoded request body
     * @throws UnsupportedEncodingException If encoding fails
     */
    private String buildRequestBody(Map<String, String> params) throws UnsupportedEncodingException {
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
}