package com.HiveGroup.HiveRH.Features.PayrollPeriod.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollPeriodStatus;

public record PayrollPeriodFilterDTO(
        Integer month,
        Integer year,
        PayrollPeriodStatus status
) {
}
