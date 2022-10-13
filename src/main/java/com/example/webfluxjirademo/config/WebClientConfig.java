package com.example.webfluxjirademo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        final String baseUrl = "https://jiacheng-li.atlassian.net/rest/agile/1.0/board";
        final String basicAuth = "Basic amlhY2hlbmcubGlAdGhvdWdodHdvcmtzLmNvbTprRmM5b1h3UmdLZEpYNXpzUGtCNkVDQ0Y=";

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuth).build();
    }
}
