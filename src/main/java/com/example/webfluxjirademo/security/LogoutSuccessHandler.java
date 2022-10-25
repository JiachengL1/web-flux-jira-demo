package com.example.webfluxjirademo.security;

import com.example.webfluxjirademo.service.AuthenticationService;
import com.example.webfluxjirademo.util.ResponseUtil;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class LogoutSuccessHandler implements ServerLogoutSuccessHandler {

    private final AuthenticationService authenticationService;

    public LogoutSuccessHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @SneakyThrows
    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        String token = exchange.getExchange().getRequest().getHeaders().getFirst("Authorization");
        authenticationService.removeAuthentication(token);

        return ResponseUtil.writeResponse(exchange.getExchange().getResponse(),
                Map.of("message", "Logout success!"));
    }
}
