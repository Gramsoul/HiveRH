package com.HiveGroup.HiveRH.Features.Account.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewAccountDTO(

        @NotBlank(message = "El usuario es obligatorio")
        @Size(
                min = 4,
                max = 30,
                message = "El usuario debe tener entre 4 y 30 caracteres"
        )
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$",
                message = "El usuario solo puede contener letras, números, punto, guion y guion bajo"
        )
        String user,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        @Size(
                max = 100,
                message = "El email no puede superar los 100 caracteres"
        )
        String email,

        @NotBlank(message = "La contraseña temporal es obligatoria")
        @Size(
                min = 3,
                max = 72,
                message = "La contraseña temporal debe tener entre 3 y 72 caracteres"
        )
        String password,

        @NotNull(message = "El rol es obligatorio")
        RolEnum rol
) {
}