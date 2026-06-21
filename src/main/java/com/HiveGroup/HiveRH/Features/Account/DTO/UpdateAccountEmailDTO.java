package com.HiveGroup.HiveRH.Features.Account.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAccountEmailDTO(

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        @Size(
                max = 100,
                message = "El email no puede superar los 100 caracteres"
        )
        String email
) {
}