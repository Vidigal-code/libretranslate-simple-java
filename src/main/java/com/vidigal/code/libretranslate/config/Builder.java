package com.vidigal.code.libretranslate.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder for creating LibreTranslateConfig instances.
 * Allows configuring parameters in a fluent way.
 *
 * @author Kauan Vidigal
 */
public class Builder {

    private static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);

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
        if (readtimeout < 3000 || readtimeout > 10000) {
            LOGGER.warn("Invalid read timeout value provided: " + readtimeout + "ms");
            LOGGER.warn("Read timeout is out of acceptable range. Reverting to default: "
                    + LibreTranslateConfig.DEFAULT_READ_TIMEOUT + "ms");
            this.readtimeout = LibreTranslateConfig.DEFAULT_READ_TIMEOUT;
        } else {
            this.readtimeout = readtimeout;
        }
        return this;
    }

    /**
     * Sets the connection timeout.
     *
     * @param timeout The timeout in milliseconds
     * @return This builder for method chaining
     */
    public Builder timeout(int timeout) {
        if (timeout < 2000 || timeout > 5000) {
            LOGGER.warn("Invalid connection timeout value provided: " + timeout + "ms");
            LOGGER.warn("Connection timeout is out of acceptable range. Reverting to default: "
                    + LibreTranslateConfig.DEFAULT_TIMEOUT + "ms");
            this.timeout = LibreTranslateConfig.DEFAULT_TIMEOUT;
        } else {
            this.timeout = timeout;
        }
        return this;
    }


    /**
     * Sets the maximum number of reconnection attempts.
     *
     * @param maxRetries The maximum number of attempts
     * @return This builder for method chaining
     */
    public Builder maxRetries(int maxRetries) {
        if (maxRetries > 5) {
            LOGGER.warn("Invalid maxRetries value provided: " + maxRetries);
            LOGGER.warn("Maximum retry attempts exceeded allowed limit. Reverting to default: 3");
            this.maxRetries = 3;
        } else {
            this.maxRetries = maxRetries;
        }
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