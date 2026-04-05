package com.example.dotalink.feature.account.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "heroes")
public class Hero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dota_hero_id", nullable = false, unique = true)
    private Integer dotaHeroId;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDotaHeroId() {
        return dotaHeroId;
    }

    public void setDotaHeroId(Integer dotaHeroId) {
        this.dotaHeroId = dotaHeroId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
