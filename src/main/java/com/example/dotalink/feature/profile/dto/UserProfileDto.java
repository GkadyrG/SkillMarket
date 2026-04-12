package com.example.dotalink.feature.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class UserProfileDto {

    @NotBlank(message = "Nickname is required")
    @Size(min = 2, max = 60, message = "Nickname must be 2-60 chars")
    private String nickname;

    @Pattern(
            regexp = "^$|^(Herald|Guardian|Crusader|Archon|Legend|Ancient|Divine|Immortal)$",
            message = "Rank must be a valid Dota rank"
    )
    private String rank;

    @Size(max = 40, message = "Region is too long")
    private String region;

    @Size(max = 40, message = "Play time is too long")
    private String playTime;

    @Size(max = 2000, message = "About is too long")
    private String about;

    @Size(max = 255, message = "Preferred roles are too long")
    private String preferredRolesText;

    private List<Long> favoriteHeroIds = new ArrayList<>();
    private List<String> favoriteHeroNames = new ArrayList<>();
    private String username;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPreferredRolesText() {
        return preferredRolesText;
    }

    public void setPreferredRolesText(String preferredRolesText) {
        this.preferredRolesText = preferredRolesText;
    }

    public List<Long> getFavoriteHeroIds() {
        return favoriteHeroIds;
    }

    public void setFavoriteHeroIds(List<Long> favoriteHeroIds) {
        this.favoriteHeroIds = favoriteHeroIds;
    }

    public List<String> getFavoriteHeroNames() {
        return favoriteHeroNames;
    }

    public void setFavoriteHeroNames(List<String> favoriteHeroNames) {
        this.favoriteHeroNames = favoriteHeroNames;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
