package com.example.dotalink.feature.profile.model;

import java.util.Arrays;
import java.util.List;

public enum DotaRegion {
    EU_WEST("EU West"),
    EU_EAST("EU East"),
    CIS("CIS"),
    NORTH_AMERICA("North America"),
    SOUTH_AMERICA("South America"),
    SOUTHEAST_ASIA("Southeast Asia"),
    CHINA("China");

    private final String displayName;

    DotaRegion(String displayName) {
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
        return Arrays.stream(values()).map(DotaRegion::getDisplayName).toList();
    }
}
