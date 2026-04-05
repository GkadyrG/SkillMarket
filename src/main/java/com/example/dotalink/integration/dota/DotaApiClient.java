package com.example.dotalink.integration.dota;

import java.util.Optional;

public interface DotaApiClient {
    Optional<DotaExternalProfile> fetchProfileBySteamId(String steamId);
}
