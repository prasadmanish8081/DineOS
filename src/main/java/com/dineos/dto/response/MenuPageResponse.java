package com.dineos.dto.response;

import java.util.List;

public record MenuPageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
}
