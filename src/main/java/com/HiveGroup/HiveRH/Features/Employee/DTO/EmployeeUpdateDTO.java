package com.HiveGroup.HiveRH.Features.Employee.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record EmployeeUpdateDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String name,
        @NotBlank(message = "El apellido es obligatorio")
        String lastName,
        @NotBlank(message = "El telefono es obligatorio")
        String phoneNumber,
        @NotNull(message = "El genero es obligatorio")
        GenreEnum genre,
        @NotBlank(message = "El DNI es obligatorio")
        String dni,
        @NotBlank(message = "La ciudad es obligatoria")
        String city,
        @NotBlank(message = "La direccion es obligatoria")
        String address,
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        LocalDate birth_date,
        @NotNull(message = "La fecha de contratacion es obligatoria")
        LocalDate hire_date,
        LocalDate termination_date,
        @NotNull(message = "El estado es obligatorio")
        StatusEnum status,
        @NotNull(message = "El salario base es obligatorio")
        @Positive(message = "El salario base debe ser positivo")
        Double base_salary,
        Long id_account,
        @NotNull(message = "La sucursal es obligatoria")
        Long id_branch
) {
}
