package com.HiveGroup.HiveRH.Common.Security.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsrfTokenContractTest {

    @Test
    void maskedResponseTokenIsAcceptedInHeaderWithRawCookie() throws Exception {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieName("XSRF-TOKEN");
        repository.setHeaderName("X-XSRF-TOKEN");

        Config config = new Config(null, null);
        CsrfFilter csrfFilter = new CsrfFilter(repository);
        csrfFilter.setRequestHandler(config.csrfTokenRequestHandler());
        csrfFilter.setRequireCsrfProtectionMatcher(request -> "POST".equals(request.getMethod()));

        MockHttpServletRequest getRequest = new MockHttpServletRequest("GET", "/api/auth/csrf");
        MockHttpServletResponse getResponse = new MockHttpServletResponse();
        AtomicReference<String> maskedToken = new AtomicReference<>();
        FilterChain csrfEndpoint = (request, response) -> {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            assertNotNull(csrfToken);
            maskedToken.set(csrfToken.getToken());
        };

        csrfFilter.doFilter(getRequest, getResponse, csrfEndpoint);

        Cookie csrfCookie = getResponse.getCookie("XSRF-TOKEN");
        assertNotNull(csrfCookie);
        assertNotNull(maskedToken.get());
        assertNotEquals(csrfCookie.getValue(), maskedToken.get());

        MockHttpServletRequest postRequest = new MockHttpServletRequest("POST", "/api/auth/login");
        postRequest.setCookies(new Cookie("XSRF-TOKEN", csrfCookie.getValue()));
        postRequest.addHeader("X-XSRF-TOKEN", maskedToken.get());
        MockHttpServletResponse postResponse = new MockHttpServletResponse();
        AtomicBoolean endpointInvoked = new AtomicBoolean(false);

        csrfFilter.doFilter(postRequest, postResponse,
                (request, response) -> endpointInvoked.set(true));

        assertTrue(endpointInvoked.get());
        assertEquals(200, postResponse.getStatus());
    }

    @Test
    void requestWithoutMaskedHeaderIsRejected() throws Exception {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieName("XSRF-TOKEN");
        repository.setHeaderName("X-XSRF-TOKEN");

        Config config = new Config(null, null);
        CsrfFilter csrfFilter = new CsrfFilter(repository);
        csrfFilter.setRequestHandler(config.csrfTokenRequestHandler());
        csrfFilter.setRequireCsrfProtectionMatcher(request -> "POST".equals(request.getMethod()));

        MockHttpServletRequest getRequest = new MockHttpServletRequest("GET", "/api/auth/csrf");
        MockHttpServletResponse getResponse = new MockHttpServletResponse();
        csrfFilter.doFilter(getRequest, getResponse, (request, response) -> {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            assertNotNull(csrfToken);
            csrfToken.getToken();
        });

        Cookie csrfCookie = getResponse.getCookie("XSRF-TOKEN");
        assertNotNull(csrfCookie);

        MockHttpServletRequest postRequest = new MockHttpServletRequest("POST", "/api/auth/login");
        postRequest.setCookies(new Cookie("XSRF-TOKEN", csrfCookie.getValue()));
        MockHttpServletResponse postResponse = new MockHttpServletResponse();
        AtomicBoolean endpointInvoked = new AtomicBoolean(false);

        csrfFilter.doFilter(postRequest, postResponse,
                (request, response) -> endpointInvoked.set(true));

        assertFalse(endpointInvoked.get());
        assertEquals(403, postResponse.getStatus());
    }
}
