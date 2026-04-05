package com.example.dotalink.integration.dota;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SteamApiProperties.class)
public class DotaApiConfig {

    private static final Logger log = LoggerFactory.getLogger(DotaApiConfig.class);

    @Bean
    public DotaApiClient dotaApiClient(RestClient.Builder restClientBuilder, SteamApiProperties properties) {
        if (properties.isEnabled() && properties.getApiKey() != null && !properties.getApiKey().isBlank()) {
            log.info("Steam API integration enabled");
            RestClient restClient = restClientBuilder
                    .baseUrl(properties.getBaseUrl())
                    .build();
            return new SteamWebApiClient(restClient, properties);
        }

        log.info("Steam API integration disabled, using stub client");
        return new StubDotaApiClient();
    }
}
