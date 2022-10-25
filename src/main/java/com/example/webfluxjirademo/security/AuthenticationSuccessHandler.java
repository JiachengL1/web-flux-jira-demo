package com.example.webfluxjirademo.security;

import com.example.webfluxjirademo.service.AuthenticationService;
import com.example.webfluxjirademo.util.ResponseUtil;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Component
public class AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;

    public AuthenticationSuccessHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @SneakyThrows
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        String token = UUID.randomUUID().toString();
        authenticationService.addAuthentication(token, authentication);

        return ResponseUtil.writeResponse(
                webFilterExchange.getExchange().getResponse(),
                Map.of("token", token));
    }
}
