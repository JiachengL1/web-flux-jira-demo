package com.example.webfluxjirademo.controller;

import com.example.webfluxjirademo.domain.issue.Fields;
import com.example.webfluxjirademo.domain.issue.Issue;
import com.example.webfluxjirademo.service.IssueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(IssueController.class)
class IssueControllerTests {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private IssueService issueService;

    @Test
    void shouldGetAllIssuesWhenRequestWithBoardId() {
        Issue issue = new Issue("item", 1, "example.com", "web", new Fields());
        when(issueService.findAllIssues(1)).thenReturn(Flux.just(issue));

        webTestClient.get()
                .uri("http://localhost:8080/issue?boardId=1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(issue);
        verify(issueService).findAllIssues(1);
    }

    @Test
    void shouldGetIssuesByStatusWhenRequestWithStatusId() {
        Issue issue = new Issue("item", 1, "example.com", "web", new Fields());
        when(issueService.findIssuesByStatus(1, 10001)).thenReturn(Flux.just(issue));

        webTestClient.get()
                .uri("http://localhost:8080/issue?boardId=1&statusId=10001")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(issue);
        verify(issueService).findIssuesByStatus(1, 10001);
    }
}
