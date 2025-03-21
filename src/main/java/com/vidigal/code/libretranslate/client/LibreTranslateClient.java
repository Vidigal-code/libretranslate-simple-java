package com.vidigal.code.libretranslate.client;

import com.vidigal.code.libretranslate.config.LibreTranslateConfig;
import com.vidigal.code.libretranslate.exception.TranslationException;
import com.vidigal.code.libretranslate.language.Language;
import com.vidigal.code.libretranslate.service.HttpResponse;
import com.vidigal.code.libretranslate.service.TranslatorService;
import com.vidigal.code.libretranslate.util.JsonUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;


/**
 * Client implementation for the LibreTranslate API providing text translation services.
 * <p>This client implements the {@link TranslatorService} interface and manages resources
 * @author Kauan Vidigal
 */
public class LibreTranslateClient implements TranslatorService  {

    private static final Logger LOGGER = Logger.getLogger(LibreTranslateClient.class.getName());

    // Constants
    public static final String DEFAULT_SOURCE_LANGUAGE = Language.AUTO.getCode();
    private static final String FORMAT_TEXT = "text";

    // Configuration
    private final LibreTranslateConfig config;

    /**
     * Constructs a new simplified LibreTranslate client with the specified configuration.
     *
     * @param config Configuration object containing API details
     * @throws TranslationException if the configuration is null
     */
    public LibreTranslateClient(LibreTranslateConfig config) {
        if (config == null) {
            throw new TranslationException("Configuration cannot be null");
        }
        this.config = config;
    }


    /**
     * Translates text to the specified target language using the default source language.
     *
     * @param text           The text to translate
     * @param targetLanguage The target language code
     * @return The translated text
     * @throws TranslationException if translation fails
     */
    @Override
    public String translate(String text, String targetLanguage) {
        if (!Language.isSupportedLanguage(targetLanguage)) {
            LOGGER.warning("Target language '" + targetLanguage + "' not supported.");
            return "";
        }
        return translate(text, DEFAULT_SOURCE_LANGUAGE, targetLanguage);
    }

    /**
     * Translates text from a specified source language to a target language.
     *
     * @param text           The text to translate
     * @param sourceLanguage The source language code
     * @param targetLanguage The target language code
     * @return The translated text
     * @throws TranslationException if translation fails
     */
    @Override
    public String translate(String text, String sourceLanguage, String targetLanguage) {
        if (!validateInputs(text, sourceLanguage, targetLanguage)) {
            return "";
        }

        try {
            Map<String, String> params = createTranslationParams(text, sourceLanguage, targetLanguage);
            HttpResponse response = sendHttpRequest(config.getApiUrl(), "POST", params);
            return handleTranslationResponse(response.getBody());
        } catch (Exception e) {
            throw new TranslationException("Translation failed", e);
        }
    }

    /**
     * Creates the parameters map for a translation request.
     *
     * @param text           The text to translate
     * @param sourceLanguage The source language code
     * @param targetLanguage The target language code
     * @return A map of request parameters
     */
    private Map<String, String> createTranslationParams(String text, String sourceLanguage, String targetLanguage) {
        Map<String, String> params = new HashMap<>();
        params.put("q", text);
        params.put("source", sourceLanguage);
        params.put("target", targetLanguage);
        params.put("format", FORMAT_TEXT);

        if (isApiKeyValid()) {
            params.put("api_key", config.getApiKey());
        }

        return params;
    }

    /**
     * Validates the input parameters for translation.
     *
     * @param text           The text to validate
     * @param sourceLanguage The source language code
     * @param targetLanguage The target language code
     * @return True if inputs are valid, false otherwise
     */
    private boolean validateInputs(String text, String sourceLanguage, String targetLanguage) {
        if (isEmpty(text)) {
            LOGGER.warning("Empty text provided");
            return false;
        }

        if (!Language.isSupportedLanguage(sourceLanguage)) {
            LOGGER.warning("Source language '" + sourceLanguage + "' not supported.");
            return false;
        }

        if (!Language.isSupportedLanguage(targetLanguage)) {
            LOGGER.warning("Target language '" + targetLanguage + "' not supported.");
            return false;
        }

        return true;
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
    private HttpResponse sendHttpRequest(String apiUrl, String method, Map<String, String> params) throws IOException {
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

    /**
     * Handles the translation response from the API.
     * Parses the JSON response and extracts the translated text.
     *
     * @param responseBody The raw response body from the API
     * @return The translated text if successful
     * @throws TranslationException If the response is invalid or contains errors
     */
    private String handleTranslationResponse(String responseBody) {
        if (isEmpty(responseBody)) {
            throw new TranslationException("Empty response from server");
        }

        try {
            Map<String, Object> jsonResponse = JsonUtil.fromJson(responseBody, Map.class);

            // Check for error response
            if (jsonResponse.containsKey("error")) {
                String errorMessage = (String) jsonResponse.get("error");
                throw new TranslationException("API returned error: " + errorMessage);
            }

            // Extract translated text
            if (jsonResponse.containsKey("translatedText")) {
                String translatedText = (String) jsonResponse.get("translatedText");
                if (isEmpty(translatedText)) {
                    throw new TranslationException("Translated text is empty in the response");
                }
                return translatedText;
            } else {
                throw new TranslationException("Unexpected response format: 'translatedText' field missing");
            }
        } catch (ClassCastException e) {
            throw new TranslationException("Unexpected data type in JSON response", e);
        }
    }

    /**
     * Checks if the API key is valid.
     *
     * @return True if the API key is valid, false otherwise
     */
    private boolean isApiKeyValid() {
        return config.getApiKey() != null && !config.getApiKey().isEmpty();
    }

    /**
     * Utility method to check if a string is null or empty.
     *
     * @param str The string to check
     * @return True if the string is null or empty, false otherwise
     */
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }


}