package com.HiveGroup.HiveRH.Features.Vacation.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;

import java.time.LocalDate;

public record VacationFilterDTO(
        AbsenceStatus status,
        LocalDate startDate,
        LocalDate endDate,
        String dniEmployee,
        String fullName
) {
}
