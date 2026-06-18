package com.HiveGroup.HiveRH.Features.Account.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;

public record ResponseAccountDTO(
        String email,
        String user,
        RolEnum rol) {
}
