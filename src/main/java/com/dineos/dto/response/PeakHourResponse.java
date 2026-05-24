package com.dineos.dto.response;

public record PeakHourResponse(
        Integer hour,
        Long orderCount
) {
}
