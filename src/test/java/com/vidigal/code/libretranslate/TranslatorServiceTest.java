package com.vidigal.code.libretranslate;

import com.vidigal.code.libretranslate.service.TranslatorService;
import org.fusesource.jansi.Ansi;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TranslatorServiceTest  {

    // Constants for test results message
    private static final String MESSAGE_PASSED = "[PASSED]";

    // API URL and Key for translation service
    private static final String API = "https://translate.fedilab.app/translate";
    private static final String KEY = "unknown";

    // Mocking TranslatorService
    private TranslatorService translatorService;

    /**
     * Setup method to initialize necessary components before each test.
     * Verifies the API connection before proceeding with any test.
     */
    @BeforeEach
    public void setUp() {
        // Check if the API is accessible before running any tests
        if (!TranslatorService.testConnection(API)) {
            System.err.println(Ansi.ansi().fg(Ansi.Color.RED).a("API connection failed. Cannot proceed with tests.").reset());
            fail("API connection failed. Cannot proceed with tests.");
        }

        // Initialize the TranslatorService with the provided API URL and Key
        translatorService = TranslatorService.create(API, KEY);
    }

    /**
     * Test for translating a text to an unsupported target language.
     * Verifies that a result is returned even if the language is unsupported.
     */
    @Test
    public void testTranslateUnsupportedTargetLanguage() {
        // Attempt translation with an unsupported target language (xx)
        String result = translatorService.translate("Hello world", "xx");
        assertNotNull(result); // Ensure that the result is not null
        System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(MESSAGE_PASSED + " Translation to multiple unsupported languages returned a result.").reset());
    }

    /**
     * Test for translating a text to unsupported multiple target languages.
     * Verifies that a result is returned even if multiple languages are unsupported.
     */
    @Test
    public void testTranslateUnsupportedTargetLanguages() {
        // Attempt translation with unsupported multiple target languages
        String result = translatorService.translate("xx", "xx", "xx");
        assertNotNull(result); // Ensure that the result is not null
        System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(MESSAGE_PASSED + " Translation to multiple unsupported languages returned a result.").reset());
    }

    /**
     * Test for checking the API connection status.
     * Verifies that the connection to the API is successful.
     */
    @Test
    public void testApiConnection() {
        try {
            // Assert that the API connection is successful
            assertTrue(TranslatorService.testConnection(API), "API connection should be successful.");
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("\n" + MESSAGE_PASSED + " API connection.").reset());
        } catch (Exception e) {
            // Handle any errors in the connection test
            fail(Ansi.ansi().fg(Ansi.Color.RED).a("Error with testApiConnection: " + e.getMessage()).reset().toString());
        }
    }

    /**
     * Lifecycle method executed after all tests.
     */
    @AfterAll
    public static void tearDown() {
        System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a("\nAll tests completed.").reset());
    }


}