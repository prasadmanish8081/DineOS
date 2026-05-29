package com.dineos.repository;

import com.dineos.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    Page<MenuItem> findByRestaurant_Id(Long restaurantId, Pageable pageable);

    Page<MenuItem> findByRestaurant_IdAndIsVeg(Long restaurantId, Boolean isVeg, Pageable pageable);

    List<MenuItem> findByRestaurant_IdAndIsAvailableTrueOrderByCategory_NameAscNameAsc(Long restaurantId);

    Optional<MenuItem> findByIdAndRestaurant_Id(Long id, Long restaurantId);

    boolean existsByNameIgnoreCaseAndRestaurant_Id(String name, Long restaurantId);

    boolean existsByNameIgnoreCaseAndRestaurant_IdAndIdNot(String name, Long restaurantId, Long id);
}
