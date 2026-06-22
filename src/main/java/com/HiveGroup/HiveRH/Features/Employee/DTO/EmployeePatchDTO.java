package com.HiveGroup.HiveRH.Features.Employee.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeePatchDTO(

        @Size(
                max = 100,
                message = "El nombre no puede superar los 100 caracteres"
        )
        @Pattern(
                regexp = "^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$",
                message = "El nombre solo puede contener letras, espacios, apóstrofes y guiones"
        )
        String name,

        @Size(
                max = 100,
                message = "El apellido no puede superar los 100 caracteres"
        )
        @Pattern(
                regexp = "^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$",
                message = "El apellido solo puede contener letras, espacios, apóstrofes y guiones"
        )
        String lastName,

        @Pattern(
                regexp = "^(?=.*\\d)[0-9+()\\-\\s]{6,20}$",
                message = "El teléfono debe tener entre 6 y 20 caracteres y contener números"
        )
        String phoneNumber,

        GenreEnum genre,

        @Pattern(
                regexp = "^\\d{7,8}$",
                message = "El DNI debe contener 7 u 8 números, sin puntos, letras ni espacios"
        )
        String dni,

        @Size(
                max = 100,
                message = "La ciudad no puede superar los 100 caracteres"
        )
        @Pattern(
                regexp = "^[\\p{L}0-9]+(?:[ .,'\\-][\\p{L}0-9]+)*$",
                message = "La ciudad contiene caracteres inválidos"
        )
        String city,

        @Size(
                min = 3,
                max = 100,
                message = "La dirección debe tener entre 3 y 100 caracteres"
        )
        @Pattern(
                regexp = ".*\\S.*",
                message = "La dirección no puede estar vacía"
        )
        String address,

        @Past(
                message = "La fecha de nacimiento debe ser anterior a la fecha actual"
        )
        LocalDate birth_date,

        @PastOrPresent(
                message = "La fecha de contratación no puede ser futura"
        )
        LocalDate hire_date,

        @PastOrPresent(
                message = "La fecha de finalización no puede ser futura"
        )
        LocalDate termination_date,

        StatusEnum status,

        @Positive(
                message = "El salario base debe ser mayor que cero"
        )
        Double base_salary,

        @Positive(
                message = "El ID de la sucursal debe ser mayor que cero"
        )
        Long id_branch

) {

    @AssertTrue(
            message = "Debe enviar al menos un campo para actualizar"
    )
    public boolean isAnyFieldPresent() {
        return name != null
                || lastName != null
                || phoneNumber != null
                || genre != null
                || dni != null
                || city != null
                || address != null
                || birth_date != null
                || hire_date != null
                || termination_date != null
                || status != null
                || base_salary != null
                || id_branch != null;
    }
}