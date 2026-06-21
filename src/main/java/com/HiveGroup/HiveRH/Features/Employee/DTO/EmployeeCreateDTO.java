package com.HiveGroup.HiveRH.Features.Employee.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeCreateDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(
                max = 100,
                message = "El nombre no puede superar los 100 caracteres"
        )
        @Pattern(
                regexp = "^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$",
                message = "El nombre solo puede contener letras, espacios, apóstrofes y guiones"
        )
        String name,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(
                max = 100,
                message = "El apellido no puede superar los 100 caracteres"
        )
        @Pattern(
                regexp = "^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$",
                message = "El apellido solo puede contener letras, espacios, apóstrofes y guiones"
        )
        String lastName,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(
                min = 6,
                max = 20,
                message = "El teléfono debe tener entre 6 y 20 caracteres"
        )
        @Pattern(
                regexp = "^[0-9+()\\-\\s]+$",
                message = "El teléfono contiene caracteres inválidos"
        )
        String phoneNumber,

        @NotNull(message = "El género es obligatorio")
        GenreEnum genre,

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(
                regexp = "^\\d{7,8}$",
                message = "El DNI debe contener 7 u 8 números, sin puntos, letras ni espacios"
        )
        String dni,

        @NotBlank(message = "La ciudad es obligatoria")
        @Size(
                max = 100,
                message = "La ciudad no puede superar los 100 caracteres"
        )
        @Pattern(
                regexp = "^[\\p{L}0-9 .,'-]+$",
                message = "La ciudad contiene caracteres inválidos"
        )
        String city,

        @NotBlank(message = "El domicilio es obligatorio")
        @Size(
                max = 100,
                message = "El domicilio no puede superar los 100 caracteres"
        )
        String address,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
        LocalDate birth_date,

        @NotNull(message = "La fecha de contratación es obligatoria")
        @PastOrPresent(
                message = "La fecha de contratación no puede ser futura"
        )
        LocalDate hire_date,

        @NotNull(message = "El salario base es obligatorio")
        @Positive(message = "El salario base debe ser mayor que cero")
        Double base_salary,

        @NotNull(message = "La sucursal es obligatoria")
        @Positive(message = "El ID de la sucursal debe ser mayor que cero")
        Long id_branch,

        @NotNull(message = "El puesto es obligatorio")
        @Positive(message = "El ID del puesto debe ser mayor que cero")
        Long id_position,

        @NotNull(message = "El departamento es obligatorio")
        @Positive(message = "El ID del departamento debe ser mayor que cero")
        Long id_department
) {
}