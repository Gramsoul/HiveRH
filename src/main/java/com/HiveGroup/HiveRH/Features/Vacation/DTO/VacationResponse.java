package com.HiveGroup.HiveRH.Features.Vacation.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;

import java.time.LocalDate;

public record VacationResponse(
        Long idVacation,
        LocalDate requestDate,
        AbsenceStatus status,
        LocalDate startDate,
        LocalDate endDate,
        Long reviewedByAccountId,
        String reviewComment,
        String dniEmployee,
        String employeeName
) {
}
