package com.HiveGroup.HiveRH.Common.Security.Auth;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AccountStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefreshTokenCookieServiceTest {

    private final RefreshTokenCookieService cookieService = new RefreshTokenCookieService(
            "hiverh_refresh",
            "/api/auth",
            true,
            "Strict"
    );

    @Test
    void createsAnHttpOnlySecureCookieScopedToAuth() {
        String cookie = cookieService.create(new IssuedRefreshToken(
                "raw-token",
                account(),
                Instant.now().plusSeconds(3600)
        ));

        assertTrue(cookie.startsWith("hiverh_refresh=raw-token"));
        assertTrue(cookie.contains("Path=/api/auth"));
        assertTrue(cookie.contains("Secure"));
        assertTrue(cookie.contains("HttpOnly"));
        assertTrue(cookie.contains("SameSite=Strict"));
    }

    @Test
    void readsTheConfiguredCookieAndClearsItWithTheSameAttributes() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("another", "ignored"),
                new Cookie("hiverh_refresh", "raw-token")
        );

        assertEquals("raw-token", cookieService.read(request).orElseThrow());

        String cleared = cookieService.clear();
        assertTrue(cleared.startsWith("hiverh_refresh="));
        assertTrue(cleared.contains("Max-Age=0"));
        assertTrue(cleared.contains("Path=/api/auth"));
        assertTrue(cleared.contains("HttpOnly"));
    }

    private AccountEntity account() {
        return AccountEntity.builder()
                .user("40111222")
                .email("40111222@hiverh.com")
                .password("encoded")
                .rol(RolEnum.EMPLOYEE)
                .status(AccountStatus.ACTIVE)
                .build();
    }
}
