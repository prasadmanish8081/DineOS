package com.dineos.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record AnalyticsDashboardResponse(
        BigDecimal totalSales,
        BigDecimal dailyRevenue,
        BigDecimal monthlyRevenue,
        Long totalOrders,
        Long completedOrders,
        Long cancelledOrders,
        List<TopSellingItemResponse> topSellingItems,
        List<RevenuePointResponse> dailyRevenueTrend,
        List<RevenuePointResponse> monthlyRevenueTrend,
        List<PeakHourResponse> peakOrderHours
) {
}
