package com.vidigal.code.example;

import com.vidigal.code.libretranslate.client.LibreTranslateClient;
import com.vidigal.code.libretranslate.service.TranslatorService;
import com.vidigal.code.libretranslate.config.LibreTranslateConfig;

/**
 * Example of how to use the LibreTranslate client.
 * This class demonstrates synchronous and asynchronous translations,
 * batch processing of commands, and custom configuration options.
 * @author Kauan Vidigal
 */
public class TranslationExample {

    // Constants for API URL and API Key
    public static final String API = "https://translate.fedilab.app/translate";
    public static final String KEY = "unknown";

    // ANSI escape codes for colored console output
    public static final String GREEN = "\u001B[32m"; // Green text
    public static final String RESET = "\u001B[0m";  // Reset text color

    /**
     * Main class to demonstrate text translation using TranslatorService
     * and LibreTranslateClient.
     */
    public static void main(String[] args) {

        // Create an instance of TranslatorService
        TranslatorService translator = TranslatorService.create(API, KEY);

        // Translate "Hello world" to Portuguese
        String result = translator.translate("Hello world", "pt");
        System.out.println(GREEN + "Translated to Portuguese: " + result + RESET);

        // Translate "Hello world" from English to Spanish
        String resultWithSource = translator.translate("Hello world", "en", "es");
        System.out.println(GREEN + "Translated to Spanish: " + resultWithSource + RESET);

        // Configure LibreTranslateClient with API settings
        LibreTranslateConfig config = LibreTranslateConfig.builder()
                .apiUrl(API)
                .apiKey(KEY)
                .readtimeout(10000)
                .timeout(5000)
                .maxRetries(4)
                .build();

        // Create an instance of LibreTranslateClient
        LibreTranslateClient client = new LibreTranslateClient(config);

        // Translate "Hello world" from English to Portuguese using LibreTranslateClient
        String resultWithConfig = client.translate("Hello world", "en", "pt");
        System.out.println(GREEN + "Translated to Portuguese Custom Config: " + resultWithConfig + RESET);
    }

}