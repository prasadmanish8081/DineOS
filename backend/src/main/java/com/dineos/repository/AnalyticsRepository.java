package com.dineos.repository;

import com.dineos.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface AnalyticsRepository extends JpaRepository<Order, Long> {

    @Query(value = """
            select coalesce(sum(o.total_amount), 0)
            from restaurant_orders o
            where o.restaurant_id = :restaurantId
              and o.payment_status = 'PAID'
            """, nativeQuery = true)
    BigDecimal totalSales(@Param("restaurantId") Long restaurantId);

    @Query(value = """
            select coalesce(sum(o.total_amount), 0)
            from restaurant_orders o
            where o.restaurant_id = :restaurantId
              and o.payment_status = 'PAID'
              and o.created_at >= :start
              and o.created_at < :end
            """, nativeQuery = true)
    BigDecimal revenueBetween(
            @Param("restaurantId") Long restaurantId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    @Query(value = """
            select count(*)
            from restaurant_orders o
            where o.restaurant_id = :restaurantId
            """, nativeQuery = true)
    Long totalOrders(@Param("restaurantId") Long restaurantId);

    @Query(value = """
            select count(*)
            from restaurant_orders o
            where o.restaurant_id = :restaurantId
              and o.status = 'CANCELLED'
            """, nativeQuery = true)
    Long cancelledOrders(@Param("restaurantId") Long restaurantId);

    @Query(value = """
            select count(*)
            from restaurant_orders o
            where o.restaurant_id = :restaurantId
              and o.status in ('SERVED', 'COMPLETED')
              and o.payment_status = 'PAID'
            """, nativeQuery = true)
    Long completedOrders(@Param("restaurantId") Long restaurantId);

    @Query(value = """
            select extract(hour from o.created_at) as hour_bucket,
                   count(*) as order_count
            from restaurant_orders o
            where o.restaurant_id = :restaurantId
            group by extract(hour from o.created_at)
            order by order_count desc, hour_bucket asc
            limit :limit
            """, nativeQuery = true)
    List<Object[]> peakOrderHours(@Param("restaurantId") Long restaurantId, @Param("limit") int limit);

    @Query(value = """
            select mi.id as menu_item_id,
                   mi.name as menu_item_name,
                   coalesce(sum(oi.quantity), 0) as quantity_sold,
                   count(distinct oi.order_id) as order_count
            from order_items oi
            join menu_items mi on mi.id = oi.menu_item_id
            join restaurant_orders o on o.id = oi.order_id
            where o.restaurant_id = :restaurantId
              and o.payment_status = 'PAID'
            group by mi.id, mi.name
            order by quantity_sold desc, order_count desc, mi.name asc
            limit :limit
            """, nativeQuery = true)
    List<Object[]> topSellingItems(@Param("restaurantId") Long restaurantId, @Param("limit") int limit);

    @Query(value = """
            select to_char(date_trunc('day', o.created_at), 'YYYY-MM-DD') as period,
                   coalesce(sum(o.total_amount), 0) as revenue
            from restaurant_orders o
            where o.restaurant_id = :restaurantId
              and o.payment_status = 'PAID'
              and o.created_at >= :start
              and o.created_at < :end
            group by date_trunc('day', o.created_at)
            order by period asc
            """, nativeQuery = true)
    List<Object[]> dailyRevenueTrend(
            @Param("restaurantId") Long restaurantId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    @Query(value = """
            select to_char(date_trunc('month', o.created_at), 'YYYY-MM') as period,
                   coalesce(sum(o.total_amount), 0) as revenue
            from restaurant_orders o
            where o.restaurant_id = :restaurantId
              and o.payment_status = 'PAID'
              and o.created_at >= :start
              and o.created_at < :end
            group by date_trunc('month', o.created_at)
            order by period asc
            """, nativeQuery = true)
    List<Object[]> monthlyRevenueTrend(
            @Param("restaurantId") Long restaurantId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}
