package com.HiveGroup.HiveRH.Features.Vacation.DTO;

import java.time.LocalDate;

public record VacationFilterDTO(
        Long idVacation,
        Boolean accepted,
        LocalDate startDate,
        LocalDate endDate,
        String fullName
) {
}
