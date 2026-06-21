package com.HiveGroup.HiveRH.Features.Department.DTO;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DepartmentFilterDTO(
        @Positive(message = "El ID del departamento debe ser mayor que cero")
        Long id_department,

        @Size(max = 100, message = "El nombre del departamento no puede superar los 100 caracteres")
        String name,

        Boolean active
) {
}
