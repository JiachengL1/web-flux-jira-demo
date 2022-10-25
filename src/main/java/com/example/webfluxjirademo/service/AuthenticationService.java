package com.example.webfluxjirademo.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AuthenticationService {

    private static final ConcurrentMap<String, Authentication> authentications = new ConcurrentHashMap<>();

    public void addAuthentication(String key, Authentication authentication) {
        authentications.put(key, authentication);
    }

    public Authentication findAuthentication(String key) {
        return authentications.get(key);
    }

    public void removeAuthentication(String key) {
        authentications.remove(key);
    }
}
