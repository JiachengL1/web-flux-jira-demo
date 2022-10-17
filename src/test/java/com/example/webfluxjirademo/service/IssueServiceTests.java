package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.User;
import com.example.webfluxjirademo.domain.comment.Comment;
import com.example.webfluxjirademo.domain.comment.CommentDetail;
import com.example.webfluxjirademo.domain.issue.Fields;
import com.example.webfluxjirademo.domain.issue.Issue;
import com.example.webfluxjirademo.domain.issue.Issues;
import com.example.webfluxjirademo.domain.status.Status;
import com.example.webfluxjirademo.exception.BoardNotFoundException;
import com.example.webfluxjirademo.exception.IssueNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IssueServiceTests {

    @InjectMocks
    private IssueService issueService;
    @Mock
    private WebClient webClient;
    @Mock
    private RequestHeadersUriSpec<?> requestHeadersUriSpec;
    @Mock
    private RequestHeadersSpec<?> requestHeadersSpec;
    @Mock
    private ResponseSpec responseSpec;

    @Test
    void shouldFetchIssuesByBoardIdAndReturnIssueFlux() {
        Issues issues = new Issues("issues", 0, 10, 1, List.of(new Issue()));

        basicMockWebClient();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issues)).when(responseSpec).bodyToMono(Issues.class);

        Flux<Issue> result = issueService.findAllIssues(1, -1, -1);

        StepVerifier.create(result)
                .expectNextMatches(new Issue()::equals)
                .verifyComplete();
        basicVerifyWebClient("/board/1/issue");
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssuesAndReturnFilteredIssueFluxByStatusId() {
        Issue issue1 = buildIssueByStatus(10001, 1.0);
        Issue issue2 = buildIssueByStatus(10002, 1.0);
        Issues issues = new Issues("issues", 0, 10, 1, List.of(issue1, issue2));

        basicMockWebClient();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issues)).when(responseSpec).bodyToMono(Issues.class);

        Flux<Issue> result = issueService.findAllIssues(1, 10002, -1);

        StepVerifier.create(result)
                .expectNextMatches(issue2::equals)
                .verifyComplete();
        basicVerifyWebClient("/board/1/issue");
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssuesAndReturnFilteredIssueFluxByPoint() {
        Issue issue1 = buildIssueByStatus(10001, 1.0);
        Issue issue2 = buildIssueByStatus(10001, 2.0);
        Issues issues = new Issues("issues", 0, 10, 1, List.of(issue1, issue2));

        basicMockWebClient();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issues)).when(responseSpec).bodyToMono(Issues.class);

        Flux<Issue> result = issueService.findAllIssues(1, -1, 2.0);

        StepVerifier.create(result)
                .expectNextMatches(issue2::equals)
                .verifyComplete();
        basicVerifyWebClient("/board/1/issue");
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssuesAndReturnFilteredIssueFluxByStatusIdAndPoint() {
        Issue issue1 = buildIssueByStatus(10001, 1.0);
        Issue issue2 = buildIssueByStatus(10001, 2.0);
        Issue issue3 = buildIssueByStatus(10002, 2.0);
        Issues issues = new Issues("issues", 0, 10, 1, List.of(issue1, issue2, issue3));

        basicMockWebClient();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issues)).when(responseSpec).bodyToMono(Issues.class);

        Flux<Issue> result = issueService.findAllIssues(1, 10001, 2.0);

        StepVerifier.create(result)
                .expectNextMatches(issue2::equals)
                .verifyComplete();
        basicVerifyWebClient("/board/1/issue");
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchErrorByInvalidBoardIdAndThrowException() {
        basicMockWebClient();
        doThrow(BoardNotFoundException.class).when(responseSpec).onStatus(any(), any());

        Throwable throwable = catchThrowable(() -> issueService.findAllIssues(1, -1, -1));

        assertThat(throwable).isExactlyInstanceOf(BoardNotFoundException.class);
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec, never()).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssueAndReturnWhole() {
        Issue issue = buildIssueByStatus(10001, 1.0);

        basicMockWebClient();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issue)).when(responseSpec).bodyToMono(Issue.class);

        Mono<Issue> result = issueService.findIssueById(issue.getId());

        StepVerifier.create(result)
                .expectNextMatches(issue::equals)
                .verifyComplete();
        basicVerifyWebClient("/issue/" + issue.getId());
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(Issue.class);
    }

    @Test
    void shouldFetchErrorByInvalidIssueIdAndThrowException() {
        basicMockWebClient();
        doThrow(IssueNotFoundException.class).when(responseSpec).onStatus(any(), any());

        Throwable throwable = catchThrowable(() -> issueService.findIssueById(1));

        assertThat(throwable).isExactlyInstanceOf(IssueNotFoundException.class);
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec, never()).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssueAndReturnCommentsPage() {
        Issue issue = buildIssueByStatus(10001, 1.0);
        CommentDetail commentDetail = new CommentDetail(1, "example.com", "my comment",
                true, Instant.EPOCH, Instant.EPOCH, new User(), new User());
        issue.getFields().setComment(new Comment(List.of(commentDetail)));

        basicMockWebClient();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issue)).when(responseSpec).bodyToMono(Issue.class);

        Flux<CommentDetail> result = issueService.findIssueCommentsById(issue.getId(), 5, 1);

        StepVerifier.create(result)
                .expectNextMatches(commentDetail::equals)
                .verifyComplete();
        basicVerifyWebClient("/issue/" + issue.getId());
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(Issue.class);
    }

    @Test
    void shouldHandleInvalidPageSizeAndPageNum() {
        Issue issue = buildIssueByStatus(10001, 1.0);
        issue.getFields().setComment(new Comment(List.of(new CommentDetail(), new CommentDetail())));

        basicMockWebClient();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issue)).when(responseSpec).bodyToMono(Issue.class);

        assertDoesNotThrow(() -> issueService.findIssueCommentsById(issue.getId(), -1, -1));
    }

    private Issue buildIssueByStatus(int statusId, double point) {
        Status status = new Status();
        status.setId(statusId);
        Fields fields = Fields.builder().status(status).storyPoint(point).build();
        return new Issue("issue", 1, "issue1", "web", fields);
    }

    private void basicMockWebClient() {
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
    }

    private void basicVerifyWebClient(String uri) {
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(uri);
        verify(requestHeadersSpec).retrieve();
    }
}
