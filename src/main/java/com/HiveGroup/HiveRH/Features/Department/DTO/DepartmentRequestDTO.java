package com.HiveGroup.HiveRH.Features.Department.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record DepartmentRequestDTO(
        @JsonAlias("departmentName")
        @NotBlank(message = "El nombre del departamento es obligatorio")
        String name
) {
}
