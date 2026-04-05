package com.example.dotalink.integration.dota;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class StubDotaApiClient implements DotaApiClient {

    private static final Logger log = LoggerFactory.getLogger(StubDotaApiClient.class);

    @Override
    public Optional<DotaExternalProfile> fetchProfileBySteamId(String steamId) {
        log.info("Stub Dota API fetch requested for steamId={}", steamId);
        return Optional.empty();
    }
}
