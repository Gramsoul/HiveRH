package com.HiveGroup.HiveRH.Features.Account;

import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;

public record AccountDTO(
        String email,
        String user,
        Long id,
        RolEnum rol
) {
}
