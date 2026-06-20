package com.HiveGroup.HiveRH.Features.Employee.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record EmployeeCreateDTO(
        String name,
        String lastName,
        String phoneNumber,
        GenreEnum genre,
        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(
                regexp = "^\\d{7,8}$",
                message = "El DNI debe contener 7 u 8 números, sin puntos, letras ni espacios"
        )
        String dni,
        String city,
        String address,
        LocalDate birth_date,
        LocalDate hire_date,
        Double base_salary,
        Long id_branch,
        Long id_position,
        Long id_department
) {
}
