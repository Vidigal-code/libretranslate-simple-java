package com.vidigal.code.libretranslate.http;

import com.vidigal.code.libretranslate.config.LibreTranslateConfig;
import com.vidigal.code.libretranslate.exception.TranslationException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Utility class for handling HTTP requests in the LibreTranslate client.
 * Manages connection, request building, and response handling using modern Java features.
 */
public class RequestHelper {
    private static final Logger LOGGER = Logger.getLogger(RequestHelper.class.getName());

    private final LibreTranslateConfig config;

    /**
     * Constructs a RequestHelper with the given configuration.
     *
     * @param config Configuration for HTTP requests.
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
    public HttpResponse<String> sendHttpRequest(String apiUrl, String method, Map<String, String> params) throws IOException {
        var maxRetries = config.getMaxRetries();
        var attempt = 0;

        while (attempt < maxRetries) {
            try {
                var client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofMillis(config.getTimeout()))
                        .build();

                var requestBuilder = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Accept", "application/json");

                if ("POST".equals(method)) {
                    var requestBody = buildRequestBody(params);
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestBody));
                } else {
                    requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
                }

                var request = requestBuilder.build();
                var response = client.send(request, HttpResponse.BodyHandlers.ofString());

                var responseCode = response.statusCode();
                var responseBody = response.body();

                if (responseCode >= 400) {
                    LOGGER.warning("HTTP request failed with code " + responseCode + ": " + responseBody);
                }

                return response;
            } catch (IOException | InterruptedException e) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw new IOException("Request failed after " + maxRetries + " attempts.", e);
                }
                LOGGER.warning("Retrying request... Attempt " + attempt + " of " + maxRetries);
            }
        }

        throw new TranslationException("Request failed after " + maxRetries + " attempts.");
    }

    /**
     * Builds the request body from parameters.
     *
     * @param params The parameters map.
     * @return The encoded request body.
     * @throws UnsupportedEncodingException If encoding fails.
     */
    private String buildRequestBody(Map<String, String> params) throws UnsupportedEncodingException {
        var result = new StringBuilder();
        var first = true;

        for (var entry : params.entrySet()) {
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