package com.HiveGroup.HiveRH.Features.Suspension.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record SuspensionResponseDTO(
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long id_suspension,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long id_employee,
        String employeeName,
        String employeeLastName,
        String motive,
        LocalDate start_date,
        LocalDate end_date
) {
}
