package com.HiveGroup.HiveRH.Features.Account.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import jakarta.validation.constraints.NotNull;

public record UpdateAccountRoleDTO(

        @NotNull(message = "El rol es obligatorio")
        RolEnum rol
) {
}