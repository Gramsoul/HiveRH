package com.HiveGroup.HiveRH.Common.Security.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
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
@EnableScheduling
@RequiredArgsConstructor
public class Config {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/accounts").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/accounts/*/rol").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/accounts/me/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/employees").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/employees").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PUT, "/api/employees/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/employees/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/employees/picture").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/employees/picture/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/branches").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/branches/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/branches/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/departments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/departments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/departments/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/positions").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/positions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/positions/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/work-schedules/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/work-schedules/me/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/work-schedules").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/work-schedules/*").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/work-schedules").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/work-schedules/**").hasAnyRole("ADMIN", "STAFF")

                        .requestMatchers(HttpMethod.POST, "/api/work-requests/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/work-requests/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/work-requests/me/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/work-requests/me/*/cancel").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/work-requests").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/work-requests/*").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/work-requests/*/approve").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/work-requests/*/reject").hasAnyRole("ADMIN", "STAFF")

                        .requestMatchers("/api/payroll-periods/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/payroll-concepts/**").hasAnyRole("ADMIN", "STAFF")

                        .requestMatchers(HttpMethod.GET, "/api/licenses").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/licenses/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/licenses").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/licenses/*").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/api/licenses/*").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/certificates").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/vacations").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/vacations").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/vacations/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/api/vacations/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/payrolls/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/payrolls/me/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/payrolls").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/payrolls/*").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/payrolls").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/payrolls/**").hasAnyRole("ADMIN", "STAFF")
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
