package com.dineos.dto.response;

import java.math.BigDecimal;

public record RevenuePointResponse(
        String period,
        BigDecimal revenue
) {
}
