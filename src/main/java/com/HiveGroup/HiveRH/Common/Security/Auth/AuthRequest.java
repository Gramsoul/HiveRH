package com.HiveGroup.HiveRH.Common.Security.Auth;

import lombok.Builder;

@Builder
public record AuthRequest(
        String identifier,
        String password
) {
}
