package com.example.dotalink.feature.profile.dto;

import java.util.ArrayList;
import java.util.List;

public class UserStatsDto {

    private String username;
    private String nickname;
    private Integer mmr;
    private List<String> favoriteHeroes = new ArrayList<>();
    private double averageRating;
    private long reviewsCount;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getMmr() {
        return mmr;
    }

    public void setMmr(Integer mmr) {
        this.mmr = mmr;
    }

    public List<String> getFavoriteHeroes() {
        return favoriteHeroes;
    }

    public void setFavoriteHeroes(List<String> favoriteHeroes) {
        this.favoriteHeroes = favoriteHeroes;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public long getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(long reviewsCount) {
        this.reviewsCount = reviewsCount;
    }
}
