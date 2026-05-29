package com.dineos.repository;

import com.dineos.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    Optional<RestaurantTable> findByIdAndRestaurant_Id(Long id, Long restaurantId);

    Optional<RestaurantTable> findByRestaurant_IdAndTableNumberIgnoreCase(Long restaurantId, String tableNumber);

    boolean existsByRestaurant_IdAndTableNumberIgnoreCase(Long restaurantId, String tableNumber);
}
