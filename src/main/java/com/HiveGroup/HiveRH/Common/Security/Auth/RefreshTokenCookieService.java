package com.HiveGroup.HiveRH.Common.Security.Auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Service
public class RefreshTokenCookieService {

    private final String cookieName;
    private final String cookiePath;
    private final boolean secure;
    private final String sameSite;

    public RefreshTokenCookieService(
            @Value("${hiverh.auth.refresh-cookie.name}") String cookieName,
            @Value("${hiverh.auth.refresh-cookie.path}") String cookiePath,
            @Value("${hiverh.auth.refresh-cookie.secure}") boolean secure,
            @Value("${hiverh.auth.refresh-cookie.same-site}") String sameSite
    ) {
        this.cookieName = cookieName;
        this.cookiePath = cookiePath;
        this.secure = secure;
        this.sameSite = sameSite;
    }

    public String create(IssuedRefreshToken refreshToken) {
        Duration maxAge = Duration.between(Instant.now(), refreshToken.expiresAt());
        if (maxAge.isNegative()) {
            maxAge = Duration.ZERO;
        }
        return build(refreshToken.token(), maxAge).toString();
    }

    public String clear() {
        return build("", Duration.ZERO).toString();
    }

    public Optional<String> read(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank())
                .findFirst();
    }

    private ResponseCookie build(String value, Duration maxAge) {
        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(cookiePath)
                .maxAge(maxAge)
                .build();
    }
}
