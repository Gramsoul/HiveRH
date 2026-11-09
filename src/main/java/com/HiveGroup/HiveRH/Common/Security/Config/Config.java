package com.HiveGroup.HiveRH.Common.Security.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableScheduling
@RequiredArgsConstructor
public class Config {

    private static final Set<String> COOKIE_AUTH_ENDPOINTS = Set.of(
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/logout"
    );

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            CookieCsrfTokenRepository csrfTokenRepository,
            CsrfTokenRequestHandler csrfTokenRequestHandler
    ) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/refresh", "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/accounts").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/accounts/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/accounts/*").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/accounts/*/rol").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/accounts/me/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/employees").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/employees").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/employees/picture").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/employees/picture/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/employees/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/employees/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasAnyRole("ADMIN", "STAFF")

                        .requestMatchers(HttpMethod.GET, "/api/branches/*").hasRole("ADMIN")
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

                        .requestMatchers(HttpMethod.GET, "/api/licenses/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/licenses").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/licenses/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/licenses").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/licenses/*").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/api/licenses/*").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/certificates").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/vacations/me").authenticated()
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
                .cors(cors -> {})
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(csrfTokenRequestHandler)
                        .requireCsrfProtectionMatcher(cookieAuthRequestMatcher()))
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

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository(
            @Value("${hiverh.auth.refresh-cookie.secure}") boolean secure,
            @Value("${hiverh.auth.refresh-cookie.same-site}") String sameSite
    ) {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieName("XSRF-TOKEN");
        repository.setHeaderName("X-XSRF-TOKEN");
        repository.setCookieCustomizer(cookie -> cookie
                .path("/")
                .secure(secure)
                .sameSite(sameSite));
        return repository;
    }

    @Bean
    public CsrfTokenRequestHandler csrfTokenRequestHandler() {
        XorCsrfTokenRequestAttributeHandler requestHandler =
                new XorCsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null);
        return requestHandler;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${hiverh.auth.cors.allowed-origins}") String allowedOrigins
    ) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private RequestMatcher cookieAuthRequestMatcher() {
        return request -> HttpMethod.POST.matches(request.getMethod())
                && COOKIE_AUTH_ENDPOINTS.contains(request.getServletPath());
    }
}
