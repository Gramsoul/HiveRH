package com.HiveGroup.HiveRH.Features.Suspension.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SuspensionRequestDTO(
        @NotNull(message = "El empleado es obligatorio")
        Long id_employee,
        @NotBlank(message = "El motivo es obligatorio")
        String motive,
        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate start_date,
        @NotNull(message = "La fecha de finalizacion es obligatoria")
        LocalDate end_date
) {
}
