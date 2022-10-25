package com.example.webfluxjirademo.security;

import com.example.webfluxjirademo.util.ResponseUtil;
import lombok.SneakyThrows;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class AuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    @SneakyThrows
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return ResponseUtil.writeResponse(
                webFilterExchange.getExchange().getResponse(),
                Map.of("message", "Login failed!"));
    }
}
