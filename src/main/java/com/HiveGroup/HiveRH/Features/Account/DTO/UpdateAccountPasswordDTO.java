package com.HiveGroup.HiveRH.Features.Account.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAccountPasswordDTO(

        @NotBlank(message = "La contraseña actual es obligatoria")
        String currentPassword,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(
                min = 8,
                max = 72,
                message = "La nueva contraseña debe tener entre 8 y 72 caracteres"
        )
        String newPassword
) {
}