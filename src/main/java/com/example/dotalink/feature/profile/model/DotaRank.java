package com.example.dotalink.feature.profile.model;

import java.util.Arrays;
import java.util.List;

public enum DotaRank {
    HERALD("Herald"),
    GUARDIAN("Guardian"),
    CRUSADER("Crusader"),
    ARCHON("Archon"),
    LEGEND("Legend"),
    ANCIENT("Ancient"),
    DIVINE("Divine"),
    IMMORTAL("Immortal");

    private final String displayName;

    DotaRank(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static String normalizeOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = raw.trim();
        return valuesList().stream()
                .filter(value -> value.equalsIgnoreCase(normalized))
                .findFirst()
                .orElse(null);
    }

    public static List<String> valuesList() {
        return Arrays.stream(values()).map(DotaRank::getDisplayName).toList();
    }
}
