package com.HiveGroup.HiveRH.Features.Vacation.DTO;

import java.time.LocalDate;

public record VacationResponse(
        Long idVacation,
        LocalDate requestDate,
        boolean accepted,
        LocalDate startDate,
        LocalDate endDate,
        boolean paid,
        Long idEmployee,
        String employeeName
) {
}