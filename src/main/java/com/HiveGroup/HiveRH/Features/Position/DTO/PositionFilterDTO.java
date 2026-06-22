package com.HiveGroup.HiveRH.Features.Position.DTO;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PositionFilterDTO(

        @Positive(message = "El ID del departamento debe ser mayor que cero")
        Long id_department,

        @Size(max = 100, message = "El nombre del puesto no puede superar los 100 caracteres")
        String name,

        Boolean active
) {
}