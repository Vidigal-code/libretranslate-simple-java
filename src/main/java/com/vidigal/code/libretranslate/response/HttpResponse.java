package com.vidigal.code.libretranslate.response;

import java.util.List;
import java.util.Map;

/**
 * Represents an HTTP response with status code, response body, and headers.
 * @author Kauan Vidigal
 */
public class HttpResponse {

    private final int statusCode;
    private final String body;
    private final Map<String, List<String>> headers;

    /**
     * Constructs an HttpResponse object with the given status code, body, and headers.
     *
     * @param statusCode The HTTP status code of the response.
     * @param body       The response body as a string.
     * @param headers    The response headers as a map of header names to values.
     */
    public HttpResponse(int statusCode, String body, Map<String, List<String>> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

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
