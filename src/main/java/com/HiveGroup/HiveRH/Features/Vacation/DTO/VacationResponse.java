package com.HiveGroup.HiveRH.Features.Vacation.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record VacationResponse(
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long idVacation,
        LocalDate requestDate,
        boolean accepted,
        LocalDate startDate,
        LocalDate endDate,
        boolean paid,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long idEmployee,
        String employeeName
) {
}
