package com.example.dotalink.feature.account.repository;

import com.example.dotalink.feature.account.model.Hero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeroRepository extends JpaRepository<Hero, Long> {
    java.util.List<Hero> findAllByOrderByNameAsc();
}
