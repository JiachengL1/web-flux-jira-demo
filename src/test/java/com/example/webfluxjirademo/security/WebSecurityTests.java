package com.example.webfluxjirademo.security;

import com.example.webfluxjirademo.controller.BoardController;
import com.example.webfluxjirademo.controller.IssueController;
import com.example.webfluxjirademo.domain.Authentication;
import com.example.webfluxjirademo.repository.AuthenticationRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

@WebFluxTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WebSecurityTests {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private BoardController boardController;
    @MockBean
    private IssueController issueController;
    @MockBean
    private AuthenticationRepository repository;

    @Test
    void shouldBeUnauthorizedWithoutToken() {
        webTestClient.get()
                .uri("http://localhost:8080/boards")
                .exchange().expectStatus().isUnauthorized();
    }

    @Test
    void shouldLoginSuccessfullyAndReturnToken() {
        Authentication authentication = new Authentication(1, "user", "password");
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", authentication.getUsername());
        formData.add("password", authentication.getPassword());

        Mockito.when(repository.findAll()).thenReturn(Flux.just(authentication));
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post().uri("http://localhost:8080/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.ALL)
                .body(BodyInserters.fromFormData(formData))
                .exchange()
                // TODO 302 status
                .expectStatus().isFound();
    }

    @Test
    void shouldPassAuthenticationWithToken() {
        Authentication authentication = new Authentication(1, "user", "password");
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", authentication.getUsername());
        formData.add("password", authentication.getPassword().substring(2));

        Mockito.when(repository.findAll()).thenReturn(Flux.just(authentication));
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post().uri("http://localhost:8080/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.ALL)
                .body(BodyInserters.fromFormData(formData))
                .exchange()
                .expectStatus().isFound();
    }
}
