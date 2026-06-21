package com.HiveGroup.HiveRH.Features.Account.DTO;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountPasswordDTO(
        @NotBlank(message = "La contrasena actual es obligatoria")
        String currentPassword,
        @NotBlank(message = "La nueva contrasena es obligatoria")
        String newPassword
) {
}
