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
import java.time.Period;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
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
        Issues issues = buildIssuesByList(new Issue());
        mockWebClientAndReturnIssues(issues);

        Flux<Issue> result = issueService.findAllIssues(1, -1, -1);

        StepVerifier.create(result)
                .expectNextMatches(new Issue()::equals)
                .verifyComplete();
        verifyWebClientWithUriAndClass("/board/1/issue", Issues.class);
    }

    @Test
    void shouldFetchIssuesAndReturnFilteredIssueFluxByStatusId() {
        Issue issue1 = buildCommonIssue(10001, 1.0, "");
        Issue issue2 = buildCommonIssue(10002, 1.0, "");
        Issues issues = buildIssuesByList(issue1, issue2);
        mockWebClientAndReturnIssues(issues);

        Flux<Issue> result = issueService.findAllIssues(1, 10002, -1);

        StepVerifier.create(result)
                .expectNextMatches(issue2::equals)
                .verifyComplete();
        verifyWebClientWithUriAndClass("/board/1/issue", Issues.class);
    }

    @Test
    void shouldFetchIssuesAndReturnFilteredIssueFluxByPoint() {
        Issue issue1 = buildCommonIssue(10001, 1.0, "");
        Issue issue2 = buildCommonIssue(10001, 2.0, "");
        Issues issues = buildIssuesByList(issue1, issue2);
        mockWebClientAndReturnIssues(issues);

        Flux<Issue> result = issueService.findAllIssues(1, -1, 2.0);

        StepVerifier.create(result)
                .expectNextMatches(issue2::equals)
                .verifyComplete();
        verifyWebClientWithUriAndClass("/board/1/issue", Issues.class);
    }

    @Test
    void shouldFetchIssuesAndReturnFilteredIssueFluxByStatusIdAndPoint() {
        Issue issue1 = buildCommonIssue(10001, 1.0, "");
        Issue issue2 = buildCommonIssue(10001, 2.0, "");
        Issue issue3 = buildCommonIssue(10002, 2.0, "");
        Issues issues = buildIssuesByList(issue1, issue2, issue3);
        mockWebClientAndReturnIssues(issues);

        Flux<Issue> result = issueService.findAllIssues(1, 10001, 2.0);

        StepVerifier.create(result)
                .expectNextMatches(issue2::equals)
                .verifyComplete();
        verifyWebClientWithUriAndClass("/board/1/issue", Issues.class);
    }

    @Test
    void shouldFetchErrorByInvalidBoardIdAndThrowException() {
        mockWebClientBasically();
        doThrow(BoardNotFoundException.class).when(responseSpec).onStatus(any(), any());

        Throwable throwable = catchThrowable(() -> issueService.findAllIssues(1, -1, -1));

        assertThat(throwable).isExactlyInstanceOf(BoardNotFoundException.class);
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec, never()).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssueAndReturnWhole() {
        Issue issue = buildCommonIssue(10001, 1.0, "");
        mockWebClientAndReturnSingleIssue(issue);

        Mono<Issue> result = issueService.findIssueById(issue.getId());

        StepVerifier.create(result)
                .expectNextMatches(issue::equals)
                .verifyComplete();
        verifyWebClientWithUriAndClass("/issue/" + issue.getId(), Issue.class);
    }

    @Test
    void shouldFetchErrorByInvalidIssueIdAndThrowException() {
        mockWebClientBasically();
        doThrow(IssueNotFoundException.class).when(responseSpec).onStatus(any(), any());

        Throwable throwable = catchThrowable(() -> issueService.findIssueById(1));

        assertThat(throwable).isExactlyInstanceOf(IssueNotFoundException.class);
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec, never()).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssueAndReturnCommentsPage() {
        Issue issue = buildCommonIssue(10001, 1.0, "");
        CommentDetail commentDetail = new CommentDetail(1, "example.com", "my comment",
                true, Instant.EPOCH, Instant.EPOCH, new User(), new User());
        issue.getFields().setComment(new Comment(List.of(commentDetail)));
        mockWebClientAndReturnSingleIssue(issue);

        Flux<CommentDetail> result = issueService.findIssueCommentsById(issue.getId(), 5, 1);

        StepVerifier.create(result)
                .expectNextMatches(commentDetail::equals)
                .verifyComplete();
        verifyWebClientWithUriAndClass("/issue/" + issue.getId(), Issue.class);
    }

    @Test
    void shouldHandleInvalidPageSizeAndPageNum() {
        Issue issue = buildCommonIssue(10001, 1.0, "");
        issue.getFields().setComment(new Comment(List.of(new CommentDetail(), new CommentDetail())));
        mockWebClientAndReturnSingleIssue(issue);

        assertDoesNotThrow(() -> issueService.findIssueCommentsById(issue.getId(), -1, -1));
    }

    @Test
    void shouldFetchIssuesAndFilterWithLabel() {
        Issue issue1 = buildCommonIssue(1001, 1.0, "empty");
        Issue issue2 = buildCommonIssue(1001, 1.0, "test");
        Issues issues = buildIssuesByList(issue1, issue2);
        mockWebClientAndReturnIssues(issues);

        Flux<Issue> result1 = issueService.findIssuesByLabel(1, "test");
        Flux<Issue> result2 = issueService.findIssuesByLabel(1, " ");

        StepVerifier.create(result1)
                .expectNextMatches(issue2::equals)
                .verifyComplete();
        StepVerifier.create(result2)
                .expectNextMatches(issue1::equals)
                .expectNextMatches(issue2::equals)
                .verifyComplete();
        verifyWebClientWithUriAndClass("/board/1/issue", Issues.class);
    }

    @Test
    void shouldFetchIssuesAndFilterWithDays() {
        Issue issue1 = buildCommonIssue(10001, 1.0, "");
        issue1.getFields().setUpdated(Instant.now().minus(Period.ofDays(1)));
        Issue issue2 = buildCommonIssue(10001, 1.0, "");
        issue2.getFields().setUpdated(Instant.now());
        Issues issues = buildIssuesByList(issue1, issue2);
        mockWebClientAndReturnIssues(issues);

        Flux<Issue> result1 = issueService.findRecentIssues(1, 1);
        Flux<Issue> result2 = issueService.findRecentIssues(1, 2);

        StepVerifier.create(result1)
                .expectNextMatches(issue2::equals)
                .verifyComplete();
        StepVerifier.create(result2)
                .expectNextMatches(issue2::equals)
                .expectNextMatches(issue1::equals)
                .verifyComplete();
        verifyWebClientWithUriAndClass("/board/1/issue", Issues.class);
    }

    private void mockWebClientAndReturnIssues(Issues issues) {
        mockWebClientBasically();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issues)).when(responseSpec).bodyToMono(Issues.class);
    }

    private void mockWebClientAndReturnSingleIssue(Issue issue) {
        mockWebClientBasically();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issue)).when(responseSpec).bodyToMono(Issue.class);
    }

    private <T> void verifyWebClientWithUriAndClass(String uri, Class<T> type) {
        verify(webClient, atLeastOnce()).get();
        verify(requestHeadersUriSpec, atLeastOnce()).uri(uri);
        verify(requestHeadersSpec, atLeastOnce()).retrieve();
        verify(responseSpec, atLeastOnce()).onStatus(any(), any());
        verify(responseSpec, atLeastOnce()).bodyToMono(type);
    }

    private Issues buildIssuesByList(Issue... issues) {
        return new Issues("issues", 0, issues.length, issues.length, List.of(issues));
    }

    private Issue buildCommonIssue(int statusId, double storyPoint, String label) {
        Status status = new Status();
        status.setId(statusId);
        Fields fields = Fields.builder()
                .status(status)
                .storyPoint(storyPoint)
                .labels(List.of(label)).build();
        return new Issue("issue", 1, "issue1", "web", fields);
    }

    private void mockWebClientBasically() {
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
    }
}
