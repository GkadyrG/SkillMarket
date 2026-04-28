package com.example.dotalink.integration.opendota;

import com.example.dotalink.common.exception.DotaAccountNotFoundException;
import com.example.dotalink.common.exception.ExternalApiException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class OpenDotaClient {

    private static final Logger log = LoggerFactory.getLogger(OpenDotaClient.class);

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final OpenDotaProperties properties;

    public OpenDotaClient(OkHttpClient okHttpClient,
                          ObjectMapper objectMapper,
                          OpenDotaProperties properties) {
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public OpenDotaPlayerResponse getPlayerByAccountId(Long accountId) {
        HttpUrl url = baseApiUrl().newBuilder()
                .addPathSegment("api")
                .addPathSegment("players")
                .addPathSegment(String.valueOf(accountId))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == 404) {
                log.warn("OpenDota player not found for accountId={}", accountId);
                throw new DotaAccountNotFoundException("Player with account_id " + accountId + " was not found in OpenDota");
            }

            if (!response.isSuccessful()) {
                log.error("OpenDota request failed: accountId={}, status={}", accountId, response.code());
                throw new ExternalApiException("OpenDota API is temporarily unavailable. Please try again later.");
            }

            if (response.body() == null) {
                log.error("OpenDota response body is empty for accountId={}", accountId);
                throw new ExternalApiException("OpenDota API returned an empty response");
            }

            OpenDotaPlayerResponse playerResponse =
                    objectMapper.readValue(response.body().byteStream(), OpenDotaPlayerResponse.class);

            if (playerResponse == null || playerResponse.profile() == null) {
                log.warn("OpenDota profile is missing for accountId={}", accountId);
                throw new DotaAccountNotFoundException("Player profile for account_id " + accountId + " was not found");
            }

            return playerResponse;
        } catch (IOException ex) {
            log.error("I/O error while requesting OpenDota for accountId={}", accountId, ex);
            throw new ExternalApiException("Failed to load player data from OpenDota", ex);
        }
    }

    public List<OpenDotaRecentMatchResponse> getRecentMatchesByAccountId(Long accountId) {
        HttpUrl url = baseApiUrl().newBuilder()
                .addPathSegment("api")
                .addPathSegment("players")
                .addPathSegment(String.valueOf(accountId))
                .addPathSegment("recentMatches")
                .build();

        return executeListRequest(url, accountId, "recent matches", new TypeReference<>() {
        });
    }

    public List<OpenDotaPlayerHeroStatsResponse> getPlayerHeroesByAccountId(Long accountId) {
        HttpUrl url = baseApiUrl().newBuilder()
                .addPathSegment("api")
                .addPathSegment("players")
                .addPathSegment(String.valueOf(accountId))
                .addPathSegment("heroes")
                .build();

        return executeListRequest(url, accountId, "player heroes", new TypeReference<>() {
        });
    }

    public List<OpenDotaHeroResponse> getHeroes() {
        HttpUrl url = baseApiUrl().newBuilder()
                .addPathSegment("api")
                .addPathSegment("heroes")
                .build();

        return executeListRequest(url, null, "heroes", new TypeReference<>() {
        });
    }

    private <T> List<T> executeListRequest(HttpUrl url,
                                           Long accountId,
                                           String resourceName,
                                           TypeReference<List<T>> typeReference) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("OpenDota {} request failed: accountId={}, status={}", resourceName, accountId, response.code());
                throw new ExternalApiException("OpenDota API is temporarily unavailable. Please try again later.");
            }

            if (response.body() == null) {
                log.error("OpenDota {} response body is empty for accountId={}", resourceName, accountId);
                throw new ExternalApiException("OpenDota API returned an empty response");
            }

            return objectMapper.readValue(response.body().byteStream(), typeReference);
        } catch (IOException ex) {
            log.error("I/O error while requesting OpenDota {} for accountId={}", resourceName, accountId, ex);
            throw new ExternalApiException("Failed to load player data from OpenDota", ex);
        }
    }

    private HttpUrl baseApiUrl() {
        HttpUrl baseUrl = HttpUrl.parse(properties.getBaseUrl());
        if (baseUrl == null) {
            throw new ExternalApiException("OpenDota base URL is configured incorrectly");
        }
        return baseUrl;
    }
}
