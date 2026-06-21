package com.HiveGroup.HiveRH.Features.Account.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AccountDTO(
        String email,
        String user,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long id,
        RolEnum rol
) {
}
