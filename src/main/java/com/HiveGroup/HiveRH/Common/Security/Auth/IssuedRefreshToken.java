package com.HiveGroup.HiveRH.Common.Security.Auth;

import com.HiveGroup.HiveRH.Features.Account.AccountEntity;

import java.time.Instant;

public record IssuedRefreshToken(
        String token,
        AccountEntity account,
        Instant expiresAt
) {
}
