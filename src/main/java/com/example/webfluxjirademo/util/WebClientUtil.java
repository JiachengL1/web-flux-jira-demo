package com.example.webfluxjirademo.util;

import com.example.webfluxjirademo.domain.Authentication;
import com.example.webfluxjirademo.repository.AuthenticationRepository;
import io.netty.util.internal.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Component
public class WebClientUtil {

    private WebClient webClient;
    private final AuthenticationRepository repository;

    public WebClientUtil(AuthenticationRepository repository) {
        String baseUrl = "https://jiacheng-li.atlassian.net/rest/agile/1.0";
        webClient = WebClient.builder().baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
        this.repository = repository;
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public void updateToken(String username) {
        webClient = webClient.mutate()
                .defaultHeader(HttpHeaders.AUTHORIZATION, generate(username)).build();
    }

    public void removeToken() {
        webClient = webClient.mutate()
                .defaultHeader(HttpHeaders.AUTHORIZATION, StringUtil.EMPTY_STRING).build();
    }

    private String generate(String username) {
        Authentication authentication = repository
                .findByUsername(username)
                .blockOptional()
                .orElse(new Authentication());

        byte[] bytes = String.format("%s:%s",
                authentication.getUsername(),
                authentication.getPassword()).getBytes();

        return Base64.getEncoder().encodeToString(bytes);
    }
}
