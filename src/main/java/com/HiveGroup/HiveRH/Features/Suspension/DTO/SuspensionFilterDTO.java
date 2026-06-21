package com.HiveGroup.HiveRH.Features.Suspension.DTO;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SuspensionFilterDTO(

        @Positive(message = "El ID del empleado debe ser mayor que cero")
        Long id_employee,

        LocalDate start_date,

        LocalDate end_date,

        @Size(max = 100, message = "El nombre completo no puede superar los 100 caracteres")
        String fullName
) {
}
