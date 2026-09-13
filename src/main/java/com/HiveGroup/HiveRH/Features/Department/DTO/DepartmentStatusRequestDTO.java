package com.HiveGroup.HiveRH.Features.Department.DTO;

import jakarta.validation.constraints.NotNull;

public record DepartmentStatusRequestDTO(
        @NotNull(message = "El estado activo es obligatorio")
        Boolean active
) {
}
