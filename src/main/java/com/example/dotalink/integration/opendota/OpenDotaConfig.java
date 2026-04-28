package com.example.dotalink.integration.opendota;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(OpenDotaProperties.class)
public class OpenDotaConfig {

    @Bean
    public OkHttpClient openDotaOkHttpClient(OpenDotaProperties properties) {
        return new OkHttpClient.Builder()
                .connectTimeout(properties.getConnectTimeoutSeconds(), TimeUnit.SECONDS)
                .callTimeout(properties.getCallTimeoutSeconds(), TimeUnit.SECONDS)
                .readTimeout(properties.getReadTimeoutSeconds(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .protocols(List.of(Protocol.HTTP_1_1))
                .build();
    }

    @Bean
    public OpenDotaClient openDotaClient(OkHttpClient openDotaOkHttpClient,
                                         ObjectMapper objectMapper,
                                         OpenDotaProperties properties) {
        return new OpenDotaClient(openDotaOkHttpClient, objectMapper, properties);
    }
}
