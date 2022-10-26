package com.example.webfluxjirademo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationServiceTests {

    private AuthenticationService authenticationService;
    private Authentication authentication;

    public AuthenticationServiceTests() {
        authenticationService = new AuthenticationService();
    }

    @BeforeEach
    void setUp() {
        authentication = new TestingAuthenticationToken("user", "password");
    }

    @Test
    void couldAddAuthenticationAndCouldRemoveIt() {
        String key = "test";

        authenticationService.addAuthentication(key, authentication);
        Authentication addResult = authenticationService.findAuthentication(key);
        assertThat(addResult).isEqualTo(authentication);

        authenticationService.removeAuthentication(key);
        Authentication removeResult = authenticationService.findAuthentication(key);
        assertThat(removeResult).isNull();
    }

}
