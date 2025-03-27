package com.vidigal.code.libretranslate.config;

/**
 * Simplified configuration for the LibreTranslate client.
 * This class uses the Builder pattern to create immutable configuration instances.
 * Provides basic options to configure the connection to the translation API.
 *
 * @author Kauan Vidigal
 */
public class LibreTranslateConfig {

    // Default values for configurations

    /**
     * Default read timeout for connections (5 seconds)
     */
    protected static final int DEFAULT_READ_TIMEOUT = 5000;

    /**
     * Default timeout for connections (5 seconds)
     */
    protected static final int DEFAULT_TIMEOUT = 5000;

    /**
     * Default maximum number of reconnection attempts
     */
    protected static final int DEFAULT_MAX_RETRIES = 3;


    // Configuration fields
    /**
     * Base URL of the LibreTranslate API
     */
    protected final String apiUrl;

    /**
     * API key for authentication (optional)
     */
    protected final String apiKey;

    /**
     * Connection readtimeout in milliseconds
     */
    protected final int readtimeout;


    /**
     * Connection timeout in milliseconds
     */
    protected final int timeout;


    /**
     * Maximum number of reconnection attempts in case of failure
     */
    protected final int maxRetries;

    /**
     * Protected constructor to enforce the use of the Builder pattern.
     *
     * @param builder The Builder used to construct this configuration
     */
    protected LibreTranslateConfig(Builder builder) {
        this.apiUrl = builder.getApiUrl();
        this.apiKey = builder.getApiKey();
        this.readtimeout = builder.getReadTimeout();
        this.timeout = builder.getTimeout();
        this.maxRetries = builder.getMaxRetries();
    }

    /**
     * Creates a new builder to construct a configuration.
     *
     * @return A new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the configured API URL.
     *
     * @return The base API URL
     */
    public String getApiUrl() {
        return apiUrl;
    }

    /**
     * Gets the configured API key.
     *
     * @return The API key for authentication
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Gets the configured connection timeout.
     *
     * @return The Read timeout in milliseconds
     */
    public int getReadTimeout() {
        return readtimeout;
    }

    /**
     * Gets the configured connection timeout.
     *
     * @return The timeout in milliseconds
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Gets the configured maximum number of reconnection attempts.
     *
     * @return The maximum number of attempts
     */
    public int getMaxRetries() {
        return maxRetries;
    }
}