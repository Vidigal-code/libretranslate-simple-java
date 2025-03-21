package com.vidigal.code.libretranslate.config;

/**
 * Builder for creating LibreTranslateConfig instances.
 * Allows configuring parameters in a fluent way.
 * @author Kauan Vidigal
 */
public class Builder {

    private String apiUrl = "";
    private String apiKey = "";
    private int readtimeout = LibreTranslateConfig.DEFAULT_READ_TIMEOUT;
    private int timeout = LibreTranslateConfig.DEFAULT_TIMEOUT;
    private int maxRetries = LibreTranslateConfig.DEFAULT_MAX_RETRIES;


    /**
     * Sets the API URL.
     *
     * @param apiUrl The base URL for the LibreTranslate API
     * @return This builder for method chaining
     */
    public Builder apiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
        return this;
    }

    /**
     * Sets the API key for authentication.
     *
     * @param apiKey The API key to use
     * @return This builder for method chaining
     */
    public Builder apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Sets the connection read timeout.
     *
     * @param readtimeout The timeout in milliseconds
     * @return This builder for method chaining
     */
    public Builder readtimeout(int readtimeout) {
        this.readtimeout = readtimeout;
        return this;
    }

    /**
     * Sets the connection timeout.
     *
     * @param timeout The timeout in milliseconds
     * @return This builder for method chaining
     */
    public Builder timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Sets the maximum number of reconnection attempts.
     *
     * @param maxRetries The maximum number of attempts
     * @return This builder for method chaining
     */
    public Builder maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
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
     * @return The timeout in milliseconds
     */
    public int getTimeout() {
        return timeout;
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
     * Gets the configured maximum number of reconnection attempts.
     *
     * @return The maximum number of attempts
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * Builds a new LibreTranslateConfig instance with the configured parameters.
     *
     * @return A new LibreTranslateConfig instance
     */
    public LibreTranslateConfig build() {
        return new LibreTranslateConfig(this);
    }
}