package com.HiveGroup.HiveRH.Features.Account.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAccountEmailDTO(

        @NotBlank(message = "El email es obligatorio")
        @Size(
                max = 100,
                message = "El email no puede superar los 100 caracteres"
        )
        @Pattern(
                regexp = "^[A-Za-z0-9]+([._%+-]?[A-Za-z0-9]+)*@[A-Za-z0-9]+([A-Za-z0-9-]*[A-Za-z0-9])?\\.[A-Za-z]{2,}$",
                message = "El email debe tener un formato válido, por ejemplo usuario@dominio.com"
        )
        String email
) {
}