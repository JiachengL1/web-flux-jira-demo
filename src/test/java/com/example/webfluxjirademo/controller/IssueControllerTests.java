package com.example.webfluxjirademo.controller;

import com.example.webfluxjirademo.domain.User;
import com.example.webfluxjirademo.domain.comment.CommentDetail;
import com.example.webfluxjirademo.domain.issue.Fields;
import com.example.webfluxjirademo.domain.issue.Issue;
import com.example.webfluxjirademo.service.IssueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(IssueController.class)
class IssueControllerTests {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private IssueService issueService;
    private Issue issue;

    @BeforeEach
    void setUp() {
        issue = new Issue("item", 1, "example.com", "web", new Fields());
    }

    @Test
    void shouldGetAllIssuesWhenRequestWithDefaultStatusIdAndPoint() {
        when(issueService.findAllIssues(issue.getId(), -1, -1)).thenReturn(Flux.just(issue));

        webTestClient.get()
                .uri("http://localhost:8080/issue?boardId={borderId}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(issue);
        verify(issueService).findAllIssues(issue.getId(), -1, -1);
    }

    @Test
    void shouldGetIssuesWhenRequestWithSpecificStatusIdAndPoint() {
        when(issueService.findAllIssues(issue.getId(), 10001, 1.0)).thenReturn(Flux.just(issue));

        webTestClient.get()
                .uri("http://localhost:8080/issue?boardId={borderId}&statusId={statusId}&point={point}",
                        issue.getId(), 10001, 1.0)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(issue);
        verify(issueService).findAllIssues(issue.getId(), 10001, 1.0);
    }

    @Test
    void shouldGetSpecificIssueWhenRequestWithId() {
        when(issueService.findIssueById(issue.getId())).thenReturn(Mono.just(issue));

        webTestClient.get()
                .uri("http://localhost:8080/issue/{id}", issue.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Issue.class)
                .isEqualTo(issue);
        verify(issueService).findIssueById(issue.getId());
    }

    @Test
    void shouldGetCommentsPageWhenRequestWithId() {
        CommentDetail commentDetail = new CommentDetail(1, "example.com", "my comment",
                true, null, null, new User(), new User());
        when(issueService.findIssueCommentsById(anyInt(), anyInt(), anyInt())).thenReturn(Flux.just(commentDetail));

        webTestClient.get()
                .uri("http://localhost:8080/issue/{id}/comments", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CommentDetail.class)
                .hasSize(1)
                .contains(commentDetail);
        verify(issueService).findIssueCommentsById(1, 5, 1);
    }
}
