package com.HiveGroup.HiveRH.Common.Security.Auth;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String identifier,
        List<String> roles,
        boolean mustChangePassword
) {
}
