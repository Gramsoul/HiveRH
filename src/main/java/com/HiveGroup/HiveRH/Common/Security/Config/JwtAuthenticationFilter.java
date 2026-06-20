package com.HiveGroup.HiveRH.Common.Security.Config;

import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            final String username = jwtService.extractUsername(jwt);

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (username != null && authentication == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (!jwtService.isTokenValid(jwt, userDetails)) {
                    writeUnauthorizedResponse(response, request);
                    return;
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                if (userDetails instanceof AccountEntity account
                        && account.isMustChangePassword()
                        && !isPasswordChangeRequest(request)) {

                    writePasswordChangeRequiredResponse(response);
                    return;
                }
            }

        } catch (JwtException | NoSuchElementException e) {
            writeUnauthorizedResponse(response, request);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPasswordChangeRequest(
            HttpServletRequest request
    ) {
        return "PATCH".equalsIgnoreCase(request.getMethod())
                && "/api/accounts/me/password"
                .equals(request.getServletPath());
    }

    private void writePasswordChangeRequiredResponse(
            HttpServletResponse response
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {
                  "status": 403,
                  "title": "Cambio de contraseña obligatorio",
                  "detail": "Debe cambiar la contraseña temporal antes de continuar"
                }
                """);

        response.getWriter().flush();
    }

    private void writeUnauthorizedResponse(
            HttpServletResponse response,
            HttpServletRequest request
    ) throws IOException {

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType("application/json");

        response.getWriter().write(
                String.format(
                        """
                        {
                          "error": "Token JWT invalido o expirado",
                          "status": %d,
                          "path": "%s"
                        }
                        """,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        request.getRequestURI()
                )
        );

        response.getWriter().flush();
    }
}