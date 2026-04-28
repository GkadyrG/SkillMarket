package com.example.dotalink.feature.profile.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum DotaRolePreference {
    CARRY("Carry"),
    MID("Mid"),
    OFFLANE("Offlane"),
    SOFT_SUPPORT("Soft Support"),
    HARD_SUPPORT("Hard Support");

    private final String displayName;

    DotaRolePreference(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static List<String> valuesList() {
        return Arrays.stream(values()).map(DotaRolePreference::getDisplayName).toList();
    }

    public static List<String> normalizeList(List<String> rawValues) {
        if (rawValues == null || rawValues.isEmpty()) {
            return List.of();
        }

        return rawValues.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .filter(valuesList()::contains)
                .distinct()
                .collect(Collectors.toList());
    }
}
