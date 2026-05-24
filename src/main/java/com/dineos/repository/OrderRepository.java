package com.dineos.repository;

import com.dineos.entity.Order;
import com.dineos.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    boolean existsByOrderNumber(String orderNumber);

    Page<Order> findByRestaurant_Id(Long restaurantId, Pageable pageable);

    Page<Order> findByRestaurant_IdAndStatusIn(Long restaurantId, List<OrderStatus> statuses, Pageable pageable);

    Optional<Order> findByIdAndRestaurant_Id(Long id, Long restaurantId);
}
