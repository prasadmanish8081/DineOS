package com.dineos.repository;

import com.dineos.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByRestaurant_Id(Long restaurantId);

    List<Category> findByRestaurant_IdOrderByNameAsc(Long restaurantId);

    Optional<Category> findByIdAndRestaurant_Id(Long id, Long restaurantId);

    boolean existsByNameIgnoreCaseAndRestaurant_Id(String name, Long restaurantId);
}
