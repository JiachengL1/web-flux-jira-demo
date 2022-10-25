package com.example.webfluxjirademo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuthenticationController {

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<String> login() {
        return Mono.just("Login first please!");
    }
}
