package com.vidigal.code.libretranslate.service;

import com.vidigal.code.libretranslate.client.LibreTranslateClient;
import com.vidigal.code.libretranslate.config.LibreTranslateConfig;

/**
 * Service interface for translation operations.
 * <p>
 * This interface defines the contract for interacting with a translation service, such as LibreTranslate.
 * It provides methods for translating text synchronously and asynchronously, testing the connection to the service,
 * and processing commands in bulk.
 *
 * @author Kauan Vidigal
 */
public interface TranslatorService {

    /**
     * Creates a new TranslatorService instance with the specified configuration.
     * <p>
     * This method allows the creation of a {@link TranslatorService} instance using a pre-configured
     * {@link LibreTranslateConfig} object. The configuration includes settings such as the API URL and API key.
     *
     * @param config Configuration object for LibreTranslate
     * @return A new instance of {@link TranslatorService}
     * @throws IllegalArgumentException If the provided configuration is null or invalid
     */
    static TranslatorService create(LibreTranslateConfig config) {
        return new LibreTranslateClient(config);
    }

    /**
     * Creates a new TranslatorService instance with the specified API URL and key.
     * <p>
     * This method simplifies the creation of a {@link TranslatorService} instance by directly providing the
     * API URL and API key. Internally, it uses the {@link TranslatorServiceFactory} to create the service.
     *
     * @param apiUrl LibreTranslate API URL
     * @param apiKey API key for the LibreTranslate service (optional, can be null)
     * @return A new instance of {@link TranslatorService}
     * @throws IllegalArgumentException If the provided API URL is null or invalid
     */
    static TranslatorService create(String apiUrl, String apiKey) {
        return TranslatorServiceFactory.create(apiUrl, apiKey);
    }


    /**
     * Creates and returns a LibreTranslateConfig object with the specified API URL and API key.
     *
     * @param apiUrl URL of the LibreTranslate API
     * @param apiKey API key for the LibreTranslate service
     * @return Configured LibreTranslateConfig object
     */
    static LibreTranslateConfig createConfig(String apiUrl, String apiKey) {
        return TranslatorServiceFactory.createConfig(apiUrl, apiKey);

    }

    /**
     * Tests the connection to the translation service provided by the given API URL.
     * <p>
     * This method checks whether the translation service is reachable at the specified URL. It internally
     * delegates the task to the {@link TranslatorServiceFactory#testConnection(String)} method.
     *
     * @param apiUrl The URL of the translation service API to be tested
     * @return {@code true} if the connection is successful, {@code false} otherwise
     * @throws IllegalArgumentException If the provided API URL is null or invalid
     */
    static boolean testConnection(String apiUrl) {
        return TranslatorServiceFactory.testConnection(apiUrl);
    }

    /**
     * Translates text to the target language with auto-detection of source language.
     * <p>
     * This method translates the provided text into the specified target language. The source language is
     * automatically detected by the translation service.
     *
     * @param text           Text to translate
     * @param targetLanguage Target language code (e.g., "en" for English, "es" for Spanish, "fr" for French)
     * @return Translated text, or {@code null} if the translation fails
     * @throws IllegalArgumentException If the provided text or target language is null or empty
     */
    String translate(String text, String targetLanguage);

    /**
     * Translates text from the source language to the target language.
     * <p>
     * This method translates the provided text from the specified source language to the target language.
     * If the source language is set to "auto", the translation service will attempt to detect the source language.
     *
     * @param text           Text to translate
     * @param sourceLanguage Source language code (e.g., "en" for English, "es" for Spanish, or "auto" for auto-detection)
     * @param targetLanguage Target language code (e.g., "en" for English, "es" for Spanish, "fr" for French)
     * @return Translated text, or {@code null} if the translation fails
     * @throws IllegalArgumentException If the provided text, source language, or target language is null or empty
     */
    String translate(String text, String sourceLanguage, String targetLanguage);


}