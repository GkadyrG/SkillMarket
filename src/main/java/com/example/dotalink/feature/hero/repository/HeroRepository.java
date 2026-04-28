package com.example.dotalink.feature.hero.repository;

import com.example.dotalink.feature.hero.model.Hero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeroRepository extends JpaRepository<Hero, Long> {
    java.util.List<Hero> findAllByOrderByNameAsc();
}
