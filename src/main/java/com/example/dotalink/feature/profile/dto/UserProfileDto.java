package com.example.dotalink.feature.profile.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "User profile request and response model")
public class UserProfileDto {

    @NotBlank(message = "Nickname is required")
    @Size(min = 2, max = 60, message = "Nickname must be 2-60 chars")
    @Schema(description = "Displayed player nickname", example = "CaptainAdmin")
    private String nickname;

    @Pattern(
            regexp = "^$|^(Herald|Guardian|Crusader|Archon|Legend|Ancient|Divine|Immortal)$",
            message = "Rank must be a valid Dota rank"
    )
    @Schema(description = "Dota rank", example = "Archon")
    private String rank;

    @Pattern(
            regexp = "^$|^(EU West|EU East|CIS|North America|South America|Southeast Asia|China)$",
            message = "Region must be a valid region"
    )
    @Schema(description = "Preferred region", example = "EU West")
    private String region;

    @Size(max = 2000, message = "About is too long")
    @Schema(description = "Free-form profile description", example = "Looking for evening ranked games")
    private String about;

    @Schema(description = "Preferred roles as text", example = "carry,mid")
    private String preferredRolesText;
    @ArraySchema(schema = @Schema(description = "Preferred role", example = "Carry"))
    private List<String> preferredRoles = new ArrayList<>();

    @ArraySchema(schema = @Schema(description = "Favorite hero id", example = "74"))
    private List<Long> favoriteHeroIds = new ArrayList<>();
    @ArraySchema(schema = @Schema(description = "Favorite hero name", example = "Invoker"))
    private List<String> favoriteHeroNames = new ArrayList<>();
    @Schema(description = "Profile owner username", example = "demo")
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

    public List<String> getPreferredRoles() {
        return preferredRoles;
    }

    public void setPreferredRoles(List<String> preferredRoles) {
        this.preferredRoles = preferredRoles;
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
