package com.HiveGroup.HiveRH.Common.Security.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class Config {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.PATCH, "/api/accounts/*/role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/accounts/me/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/employees").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.GET, "/api/employees/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/employees").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.PUT, "/api/employees/**").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.PATCH, "/api/employees/**").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.GET, "/api/employees/page").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.POST, "/api/branch").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/branch/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/branch/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/department").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/department/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/department/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/department/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/position").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/position/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/position/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/position/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/variations").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/variations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/variations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/variations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/suspension/**").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.POST, "/api/suspension").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.GET, "/api/license").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.GET, "/api/license/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/license").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/license").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.DELETE, "/api/license/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/vacation").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.POST, "/api/vacation").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/vacation/**").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.DELETE, "/api/vacation/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/complaint").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.POST, "/api/complaint").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/complaint/**").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.PATCH, "/api/complaint/**").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.GET, "/api/payrolls").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.GET, "/api/payrolls/employee/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/payrolls").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.PUT, "/api/payrolls/**").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers(HttpMethod.DELETE, "/api/payrolls/**").hasAnyRole("ADMIN", "RRHH")
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers
                        -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .sessionManagement(manager ->
                        manager.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(restAuthenticationEntryPoint));
        return http.build();
    }
}
