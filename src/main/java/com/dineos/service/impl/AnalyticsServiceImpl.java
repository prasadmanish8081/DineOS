package com.dineos.service.impl;

import com.dineos.dto.response.AnalyticsDashboardResponse;
import com.dineos.dto.response.PeakHourResponse;
import com.dineos.dto.response.RevenuePointResponse;
import com.dineos.dto.response.TopSellingItemResponse;
import com.dineos.entity.Restaurant;
import com.dineos.entity.User;
import com.dineos.enums.Role;
import com.dineos.exception.AnalyticsAccessDeniedException;
import com.dineos.exception.RestaurantNotFoundException;
import com.dineos.exception.ResourceNotFoundException;
import com.dineos.repository.AnalyticsRepository;
import com.dineos.repository.RestaurantRepository;
import com.dineos.repository.UserRepository;
import com.dineos.service.AnalyticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public AnalyticsServiceImpl(
            AnalyticsRepository analyticsRepository,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository
    ) {
        this.analyticsRepository = analyticsRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDashboardResponse getRestaurantAnalytics(Long restaurantId, String actorEmail) {
        Restaurant restaurant = findRestaurant(restaurantId);
        User actor = findUser(actorEmail);
        ensureCanViewAnalytics(actor, restaurant);

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate firstDayNextMonth = firstDayOfMonth.plusMonths(1);
        LocalDate thirtyDaysAgo = today.minusDays(29);
        LocalDate tomorrow = today.plusDays(1);
        LocalDate firstDayOfNextYear = today.with(TemporalAdjusters.firstDayOfYear()).plusYears(1);

        Instant dayStart = today.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant dayEnd = tomorrow.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant monthStart = firstDayOfMonth.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant monthEnd = firstDayNextMonth.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant trendStart = thirtyDaysAgo.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant trendEnd = dayEnd;
        Instant yearlyStart = today.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant yearlyEnd = firstDayOfNextYear.atStartOfDay(ZoneOffset.UTC).toInstant();

        BigDecimal totalSales = scale(analyticsRepository.totalSales(restaurantId));
        BigDecimal dailyRevenue = scale(analyticsRepository.revenueBetween(restaurantId, dayStart, dayEnd));
        BigDecimal monthlyRevenue = scale(analyticsRepository.revenueBetween(restaurantId, monthStart, monthEnd));
        Long totalOrders = analyticsRepository.totalOrders(restaurantId);
        Long completedOrders = analyticsRepository.completedOrders(restaurantId);
        Long cancelledOrders = analyticsRepository.cancelledOrders(restaurantId);

        List<TopSellingItemResponse> topSellingItems = analyticsRepository.topSellingItems(restaurantId, 10)
                .stream()
                .map(row -> new TopSellingItemResponse(
                        toLong(row[0]),
                        row[1] == null ? null : String.valueOf(row[1]),
                        toLong(row[2]),
                        toLong(row[3])
                ))
                .toList();

        List<RevenuePointResponse> dailyTrend = analyticsRepository.dailyRevenueTrend(restaurantId, trendStart, trendEnd)
                .stream()
                .map(row -> new RevenuePointResponse(
                        row[0] == null ? null : String.valueOf(row[0]),
                        scale(row[1])
                ))
                .toList();

        List<RevenuePointResponse> monthlyTrend = analyticsRepository.monthlyRevenueTrend(restaurantId, yearlyStart, yearlyEnd)
                .stream()
                .map(row -> new RevenuePointResponse(
                        row[0] == null ? null : String.valueOf(row[0]),
                        scale(row[1])
                ))
                .toList();

        List<PeakHourResponse> peakHours = analyticsRepository.peakOrderHours(restaurantId, 5)
                .stream()
                .map(row -> new PeakHourResponse(
                        toInteger(row[0]),
                        toLong(row[1])
                ))
                .toList();

        return new AnalyticsDashboardResponse(
                totalSales,
                dailyRevenue,
                monthlyRevenue,
                totalOrders,
                completedOrders,
                cancelledOrders,
                topSellingItems,
                dailyTrend,
                monthlyTrend,
                peakHours
        );
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void ensureCanViewAnalytics(User actor, Restaurant restaurant) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        if (actor.getRole() != Role.OWNER || restaurant.getOwner() == null ||
                !restaurant.getOwner().getEmail().equalsIgnoreCase(actor.getEmail())) {
            throw new AnalyticsAccessDeniedException("You are not allowed to access this analytics dashboard");
        }
    }

    private BigDecimal scale(Object value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal.setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP);
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.valueOf(String.valueOf(value));
    }
}
