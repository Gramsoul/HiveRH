package com.HiveGroup.HiveRH.Features.PayrollPeriod.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollPeriodStatus;

import java.time.LocalDateTime;

public record PayrollPeriodResponseDTO(
        Long idPayrollPeriod,
        Integer month,
        Integer year,
        PayrollPeriodStatus status,
        LocalDateTime createdAt,
        LocalDateTime closedAt
) {
}
