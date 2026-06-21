package com.HiveGroup.HiveRH.Features.Suspension.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SuspensionRequestDTO(

        @NotNull(message = "El ID del empleado es obligatorio")
        @Positive(message = "El ID del empleado debe ser mayor que cero")
        Long id_employee,

        @NotBlank(message = "El motivo de la suspensión es obligatorio")
        @Size(max = 255, message = "El motivo no puede superar los 255 caracteres")
        String motive,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate start_date,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate end_date
) {
}