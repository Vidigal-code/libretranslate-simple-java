package com.vidigal.code.libretranslate.client;

import com.vidigal.code.libretranslate.config.LibreTranslateConfig;
import com.vidigal.code.libretranslate.exception.TranslationException;
import com.vidigal.code.libretranslate.http.HttpRequestHandler;
import com.vidigal.code.libretranslate.language.Language;
import com.vidigal.code.libretranslate.service.TranslatorService;
import com.vidigal.code.libretranslate.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Client implementation for the LibreTranslate API providing text translation services.
 * <p>This client implements the {@link TranslatorService} interface and manages resources
 *
 * @author Kauan Vidigal
 */
public class LibreTranslateClient implements TranslatorService {

    // Constants
    public static final String DEFAULT_SOURCE_LANGUAGE = Language.AUTO.getCode();
    private static final Logger LOGGER = Logger.getLogger(LibreTranslateClient.class.getName());
    private static final String FORMAT_TEXT = "text";

    // Configuration
    private final LibreTranslateConfig config;
    private final HttpRequestHandler httpRequestHandler;

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
        this.httpRequestHandler = new HttpRequestHandler(config);
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
            var response = httpRequestHandler.sendHttpRequest(config.getApiUrl(), "POST", params);
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