package com.example.dotalink.feature.dotaaccount.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DotaAccountForm {

    @Pattern(regexp = "^[0-9]{8,20}$", message = "Steam ID must contain 8-20 digits")
    private String steamId;

    @Pattern(regexp = "^$|^[0-9]{1,20}$", message = "Account ID must contain only digits")
    private String accountId;

    @Pattern(regexp = "^$|^https?://.*$", message = "Profile URL must start with http:// or https://")
    @Size(max = 512, message = "Profile URL is too long")
    private String profileUrl;

    @Pattern(regexp = "^$|^https?://.*$", message = "Avatar URL must start with http:// or https://")
    @Size(max = 512, message = "Avatar URL is too long")
    private String avatarUrl;

    public String getSteamId() {
        return steamId;
    }

    public void setSteamId(String steamId) {
        this.steamId = steamId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
