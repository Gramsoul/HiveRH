package com.HiveGroup.HiveRH.Common.Security.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AuthRequest(

        @NotBlank(message = "El usuario o email es obligatorio")
        @Size(
                max = 100,
                message = "El usuario o email no puede superar los 100 caracteres"
        )
        String identifier,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(
                max = 72,
                message = "La contraseña no puede superar los 72 caracteres"
        )
        String password
) {
}