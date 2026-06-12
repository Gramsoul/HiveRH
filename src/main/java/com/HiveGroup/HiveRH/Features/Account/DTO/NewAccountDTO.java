package com.HiveGroup.HiveRH.Features.Account.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;

public record NewAccountDTO(
        String user,
        String email,
        String password,
        RolEnum rol
) {
}
