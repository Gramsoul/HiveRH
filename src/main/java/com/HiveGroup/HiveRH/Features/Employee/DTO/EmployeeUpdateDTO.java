package com.HiveGroup.HiveRH.Features.Employee.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeUpdateDTO(

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
        @Pattern(
                regexp = "^(?=.*\\d)[0-9+()\\-\\s]{6,20}$",
                message = "El teléfono debe tener entre 6 y 20 caracteres y contener números"
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
                regexp = "^[\\p{L}0-9]+(?:[ .,'\\-][\\p{L}0-9]+)*$",
                message = "La ciudad contiene caracteres inválidos"
        )
        String city,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(
                min = 3,
                max = 100,
                message = "La dirección debe tener entre 3 y 100 caracteres"
        )
        String address,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(
                message = "La fecha de nacimiento debe ser anterior a la fecha actual"
        )
        LocalDate birth_date,

        @NotNull(message = "La fecha de contratación es obligatoria")
        @PastOrPresent(
                message = "La fecha de contratación no puede ser futura"
        )
        LocalDate hire_date,

        @PastOrPresent(
                message = "La fecha de finalización no puede ser futura"
        )
        LocalDate termination_date,

        @NotNull(message = "El estado del empleado es obligatorio")
        StatusEnum status,

        @NotNull(message = "El salario base es obligatorio")
        @Positive(
                message = "El salario base debe ser mayor que cero"
        )
        Double base_salary,

        @NotNull(message = "La sucursal es obligatoria")
        @Positive(
                message = "El ID de la sucursal debe ser mayor que cero"
        )
Long id_branch
) {
}