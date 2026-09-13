package com.HiveGroup.HiveRH.Features.Payroll.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollStatus;

public record PayrollFilterDTO(
        Long periodId,
        Integer month,
        Integer year,
        PayrollStatus status,
        String dniEmployee
) {
}
