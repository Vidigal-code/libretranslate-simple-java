package com.vidigal.code.libretranslate.http;

import java.util.List;
import java.util.Map;

/**
 * Represents an HTTP response with status code, response body, and headers.
 *
 * @author Kauan Vidigal
 */
public record HttpResponse(int statusCode, String body, Map<String, List<String>> headers) {

    /**
     * Retrieves the HTTP status code of the response.
     *
     * @return The HTTP status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Retrieves the response body as a string.
     *
     * @return The response body.
     */
    public String getBody() {
        return body;
    }

    /**
     * Retrieves the response headers.
     *
     * @return A map containing the response headers.
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}