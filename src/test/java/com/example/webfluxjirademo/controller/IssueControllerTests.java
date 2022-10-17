package com.example.webfluxjirademo.controller;

import com.example.webfluxjirademo.domain.User;
import com.example.webfluxjirademo.domain.comment.CommentDetail;
import com.example.webfluxjirademo.domain.issue.Fields;
import com.example.webfluxjirademo.domain.issue.Issue;
import com.example.webfluxjirademo.service.IssueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyInt;
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
                .uri("http://localhost:8080/issue?boardId={borderId}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(issue);
        verify(issueService).findAllIssues(1);
    }

    @Test
    void shouldGetIssuesWhenRequestWithBoardIdAndStatusId() {
        Issue issue = new Issue("item", 1, "example.com", "web", new Fields());
        when(issueService.findIssuesByStatus(1, 10001)).thenReturn(Flux.just(issue));

        webTestClient.get()
                .uri("http://localhost:8080/issue?boardId={borderId}&statusId={statusId}", 1, 10001)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(issue);
        verify(issueService).findIssuesByStatus(1, 10001);
    }

    @Test
    void shouldGetIssuesWhenRequestWithBoardIdAndPoint() {
        Issue issue = new Issue("item", 1, "example.com", "web", new Fields());
        when(issueService.findIssuesByPoint(1, 1)).thenReturn(Flux.just(issue));

        webTestClient.get()
                .uri("http://localhost:8080/issue?boardId={borderId}&point={point}", 1, 1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(1)
                .contains(issue);
        verify(issueService).findIssuesByPoint(1, 1);
    }

    @Test
    void shouldGetSpecificIssueWhenRequestWithId() {
        Issue issue = new Issue("item", 1, "example.com", "web", new Fields());
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
                true, Instant.EPOCH, Instant.EPOCH, new User(), new User());
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
