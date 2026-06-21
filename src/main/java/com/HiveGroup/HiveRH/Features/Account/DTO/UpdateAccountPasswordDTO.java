package com.HiveGroup.HiveRH.Features.Account.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAccountPasswordDTO(

        @NotBlank(message = "La contraseña actual es obligatoria")
        String currentPassword,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(
                min = 6,
                max = 72,
                message = "La nueva contraseña debe tener entre 6 y 72 caracteres"
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "La nueva contraseña debe contener al menos una mayúscula, una minúscula y un número"
        )
        String newPassword
) {
}