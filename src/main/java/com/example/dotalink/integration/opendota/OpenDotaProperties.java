package com.example.dotalink.integration.opendota;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.opendota")
public class OpenDotaProperties {

    private String baseUrl = "https://api.opendota.com";
    private long connectTimeoutSeconds = 10;
    private long readTimeoutSeconds = 30;
    private long callTimeoutSeconds = 40;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public long getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public void setConnectTimeoutSeconds(long connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }

    public long getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    public void setReadTimeoutSeconds(long readTimeoutSeconds) {
        this.readTimeoutSeconds = readTimeoutSeconds;
    }

    public long getCallTimeoutSeconds() {
        return callTimeoutSeconds;
    }

    public void setCallTimeoutSeconds(long callTimeoutSeconds) {
        this.callTimeoutSeconds = callTimeoutSeconds;
    }
}
