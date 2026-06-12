package com.HiveGroup.HiveRH.Features.Payroll.DTO;

import java.time.LocalDate;

public record PayrollFilterDTO(
        LocalDate startDate,
        LocalDate endDate
) {
}
