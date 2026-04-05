package com.example.dotalink.integration.dota;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.util.Optional;

public class SteamWebApiClient implements DotaApiClient {

    private static final Logger log = LoggerFactory.getLogger(SteamWebApiClient.class);
    private static final BigInteger STEAM_ID_OFFSET = new BigInteger("76561197960265728");

    private final RestClient restClient;
    private final SteamApiProperties properties;

    public SteamWebApiClient(RestClient restClient, SteamApiProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    @Override
    public Optional<DotaExternalProfile> fetchProfileBySteamId(String steamId) {
        try {
            JsonNode root = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/ISteamUser/GetPlayerSummaries/v0002/")
                            .queryParam("key", properties.getApiKey())
                            .queryParam("steamids", steamId)
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            if (root == null) {
                return Optional.empty();
            }

            JsonNode players = root.path("response").path("players");
            if (!players.isArray() || players.isEmpty()) {
                return Optional.empty();
            }

            JsonNode player = players.get(0);
            String profileUrl = asText(player.path("profileurl"));
            String avatarUrl = asText(player.path("avatarfull"));
            String accountId = toAccountId(steamId);

            return Optional.of(new DotaExternalProfile(steamId, accountId, profileUrl, avatarUrl, null));
        } catch (Exception ex) {
            log.error("Steam API request failed for steamId={}", steamId, ex);
            return Optional.empty();
        }
    }

    private String asText(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        String value = node.asText();
        return value == null || value.isBlank() ? null : value;
    }

    private String toAccountId(String steamId64) {
        try {
            BigInteger steam64 = new BigInteger(steamId64);
            return steam64.subtract(STEAM_ID_OFFSET).toString();
        } catch (Exception ignored) {
            return null;
        }
    }
}
