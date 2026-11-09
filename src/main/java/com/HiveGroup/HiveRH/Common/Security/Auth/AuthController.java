package com.HiveGroup.HiveRH.Common.Security.Auth;

import com.HiveGroup.HiveRH.Common.Security.Config.JwtService;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountService;
import com.HiveGroup.HiveRH.Features.Account.DTO.NewAccountDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.ResponseAccountDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "01 Auth", description = "Authentication and account registration.")
public class AuthController {
    private final AuthService authService;
    private final AccountService accountService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenCookieService refreshTokenCookieService;

    @GetMapping("/api/auth/csrf")
    @SecurityRequirements
    @Operation(summary = "Get CSRF token", description = "Returns an XOR-masked CSRF token that browser clients must send unchanged in X-XSRF-TOKEN for login, refresh, and logout. Its value intentionally differs from the raw XSRF-TOKEN cookie.")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @PostMapping("/api/auth/login")
    @SecurityRequirements
    @Operation(summary = "Log in", description = "Validates credentials, returns a short-lived access token, and sets a rotating refresh-token cookie.")
    public ResponseEntity<AuthResponse> authenticateUser(
            @Valid @RequestBody AuthRequest authRequest,
            @Parameter(description = "Masked token returned by GET /api/auth/csrf")
            @RequestHeader("X-XSRF-TOKEN") String csrfToken
    ) {
        AccountEntity account = authService.authenticate(authRequest);
        String accessToken = jwtService.generateToken(account);
        IssuedRefreshToken refreshToken = refreshTokenService.issue(account);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieService.create(refreshToken))
                .body(toAuthResponse(account, accessToken));
    }

    @PostMapping("/api/auth/refresh")
    @SecurityRequirements
    @Operation(summary = "Refresh access token", description = "Rotates the HttpOnly refresh-token cookie and returns a new short-lived access token.")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletRequest request,
            @Parameter(description = "Masked token returned by GET /api/auth/csrf")
            @RequestHeader("X-XSRF-TOKEN") String csrfToken
    ) {
        String rawRefreshToken = refreshTokenCookieService.read(request)
                .orElseThrow(() -> new BadCredentialsException("No se encontro la cookie de refresh token"));
        IssuedRefreshToken refreshToken = refreshTokenService.rotate(rawRefreshToken);
        String accessToken = jwtService.generateToken(refreshToken.account());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieService.create(refreshToken))
                .body(toAuthResponse(refreshToken.account(), accessToken));
    }

    @PostMapping("/api/auth/logout")
    @SecurityRequirements
    @Operation(summary = "Log out", description = "Revokes the refresh-token family and clears its cookie.")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            @Parameter(description = "Masked token returned by GET /api/auth/csrf")
            @RequestHeader("X-XSRF-TOKEN") String csrfToken
    ) {
        refreshTokenCookieService.read(request).ifPresent(refreshTokenService::revoke);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieService.clear())
                .build();
    }

    @PostMapping("/api/auth/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register account", description = "Creates a user account without linking it to an employee. Requires an ADMIN token.")
    public ResponseEntity<ResponseAccountDTO> registerUser(@Valid @RequestBody NewAccountDTO newAccountDTO) {
        return new ResponseEntity<>(accountService.save(newAccountDTO), HttpStatus.CREATED);
    }

    private AuthResponse toAuthResponse(AccountEntity account, String accessToken) {
        return new AuthResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationSeconds(),
                account.getUsername(),
                account.getAuthorities().stream().map(authority -> authority.getAuthority()).toList(),
                authService.mustChangePassword(account)
        );
    }
}
