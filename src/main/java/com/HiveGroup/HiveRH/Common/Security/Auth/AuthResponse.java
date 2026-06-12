package com.HiveGroup.HiveRH.Common.Security.Auth;

public record AuthResponse(
        String token,
        String identifier
) {
}
