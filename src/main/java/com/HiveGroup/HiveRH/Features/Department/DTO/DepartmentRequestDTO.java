package com.HiveGroup.HiveRH.Features.Department.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentRequestDTO(

        @JsonAlias("departmentName")
        @NotBlank(message = "El nombre del departamento es obligatorio")
        @Size(
                max = 100,
                message = "El nombre del departamento no puede superar los 100 caracteres"
        )
        String name
) {
}