package com.dineos.repository;

import com.dineos.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findBySlugIgnoreCase(String slug);

    Optional<Restaurant> findByOwner_EmailIgnoreCase(String email);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsByOwner_EmailIgnoreCase(String email);
}
