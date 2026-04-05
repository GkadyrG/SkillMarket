package com.example.dotalink.feature.account.model;

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

    private final String value;

    DotaRank(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String normalizeOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return Arrays.stream(values())
                .map(DotaRank::getValue)
                .filter(value -> value.equalsIgnoreCase(raw.trim()))
                .findFirst()
                .orElse(null);
    }

    public static List<String> valuesList() {
        return Arrays.stream(values()).map(DotaRank::getValue).toList();
    }
}
