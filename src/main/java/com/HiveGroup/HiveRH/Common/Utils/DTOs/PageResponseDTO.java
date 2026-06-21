package com.HiveGroup.HiveRH.Common.Utils.DTOs;

import java.util.List;

public record PageResponseDTO<T>(
        List<T> element,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
