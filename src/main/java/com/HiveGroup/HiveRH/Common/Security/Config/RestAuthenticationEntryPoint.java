package com.HiveGroup.HiveRH.Common.Security.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws
            IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String errorMessage;

        if (authException instanceof BadCredentialsException) {
            errorMessage = "Credenciales inválidas";
        } else if (authException instanceof DisabledException) {
            errorMessage = "Cuenta deshabilitada";
        } else if (authException instanceof LockedException) {
            errorMessage = "Cuenta bloqueada";
        } else if (authException instanceof AccountExpiredException) {
            errorMessage = "Cuenta expirada";
        } else if (authException instanceof CredentialsExpiredException) {
            errorMessage = "Credenciales expiradas";
        } else if (authException instanceof InsufficientAuthenticationException) {
            errorMessage = "Autenticación insuficiente";
        } else if (authException instanceof AuthenticationServiceException) {
            errorMessage = "Error en el servicio de autenticación";
        } else {
            errorMessage = "Error de autenticación: " + authException.getMessage();
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("error", errorMessage);
        responseData.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        responseData.put("path", request.getRequestURI());
        response.getWriter().write(mapper.writeValueAsString(responseData));
        response.getWriter().flush();
    }
}
