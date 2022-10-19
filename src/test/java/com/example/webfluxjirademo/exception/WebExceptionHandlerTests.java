package com.example.webfluxjirademo.exception;

import com.example.webfluxjirademo.controller.BoardController;
import com.example.webfluxjirademo.controller.IssueController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest
class WebExceptionHandlerTests {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private BoardController boardController;
    @MockBean
    private IssueController issueController;

    @Test
    void shouldThrowBoardNotFoundExceptionAndReturnMessage() {
        when(boardController.getBoardById(anyInt())).thenThrow(new BoardNotFoundException());

        webTestClient.get()
                .uri("http://localhost:8080/boards/1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Invalid board id!");
        verify(boardController).getBoardById(1);
    }

    @Test
    void shouldThrowIssueNotFoundExceptionAndReturnMessage() {
        when(issueController.getIssueById(anyInt())).thenThrow(new IssueNotFoundException());

        webTestClient.get()
                .uri("http://localhost:8080/issues/1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Invalid issue id!");
        verify(issueController).getIssueById(1);
    }
}
