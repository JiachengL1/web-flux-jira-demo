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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.Instant;
import java.time.Period;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
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

    @BeforeEach
    void setUp() {
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
    }

    @AfterEach
    void tearDown() {
        verify(webClient, atLeastOnce()).get();
        verify(requestHeadersSpec, atLeastOnce()).retrieve();
        verify(responseSpec, atLeastOnce()).onStatus(any(), any());
    }

    @Test
    void shouldFetchIssuesByBoardIdAndReturnIssueFlux() {
        Issues issues = buildIssuesByList(new Issue());

        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issues)).when(responseSpec).bodyToMono(Issues.class);

        Flux<Issue> result = issueService.findAllIssues(1, -1, -1);

        result.subscribe(res -> assertThat(res).isEqualTo(new Issue()));
        verify(requestHeadersUriSpec, atLeastOnce()).uri("/board/1/issue");
        verify(responseSpec, atLeastOnce()).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssuesAndReturnFilteredIssueFluxByStatusId() {
        Issue issueInProgress = buildCommonIssue(10001, 1.0, "");
        Issue issueDone = buildCommonIssue(10002, 1.0, "");
        Issues issues = buildIssuesByList(issueInProgress, issueDone);

        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issues)).when(responseSpec).bodyToMono(Issues.class);

        Flux<Issue> result = issueService.findAllIssues(1, 10002, -1);

        result.subscribe(res -> assertThat(res).isEqualTo(issueDone));
        verify(requestHeadersUriSpec, atLeastOnce()).uri("/board/1/issue");
        verify(responseSpec, atLeastOnce()).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssuesAndReturnFilteredIssueFluxByPoint() {
        Issue onePointIssue = buildCommonIssue(10001, 1.0, "");
        Issue twoPointsIssue = buildCommonIssue(10001, 2.0, "");
        Issues issues = buildIssuesByList(onePointIssue, twoPointsIssue);

        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issues)).when(responseSpec).bodyToMono(Issues.class);

        Flux<Issue> result = issueService.findAllIssues(1, -1, 2.0);

        result.subscribe(res -> assertThat(res).isEqualTo(twoPointsIssue));
        verify(requestHeadersUriSpec, atLeastOnce()).uri("/board/1/issue");
        verify(responseSpec, atLeastOnce()).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssuesAndReturnFilteredIssueFluxByStatusIdAndPoint() {
        Issue issueCondition1 = buildCommonIssue(10001, 1.0, "");
        Issue issueCondition1And2 = buildCommonIssue(10001, 2.0, "");
        Issue issueCondition2 = buildCommonIssue(10002, 2.0, "");
        Issues issues = buildIssuesByList(issueCondition1, issueCondition1And2, issueCondition2);

        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issues)).when(responseSpec).bodyToMono(Issues.class);

        Flux<Issue> result = issueService.findAllIssues(1, 10001, 2.0);

        result.subscribe(res -> assertThat(res).isEqualTo(issueCondition1And2));
        verify(requestHeadersUriSpec, atLeastOnce()).uri("/board/1/issue");
        verify(responseSpec, atLeastOnce()).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchErrorByInvalidBoardIdAndThrowException() {
        doThrow(BoardNotFoundException.class).when(responseSpec).onStatus(any(), any());

        Throwable throwable = catchThrowable(() -> issueService.findAllIssues(1, -1, -1));

        assertThat(throwable).isExactlyInstanceOf(BoardNotFoundException.class);
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec, never()).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssueAndReturnWhole() {
        Issue issue = buildCommonIssue(10001, 1.0, "");

        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issue)).when(responseSpec).bodyToMono(Issue.class);

        Mono<Issue> result = issueService.findIssueById(issue.getId());

        result.subscribe(res -> assertThat(res).isEqualTo(issue));
        verify(requestHeadersUriSpec, atLeastOnce()).uri("/issue/" + issue.getId());
        verify(responseSpec, atLeastOnce()).bodyToMono(Issue.class);
    }

    @Test
    void shouldFetchErrorByInvalidIssueIdAndThrowException() {
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

        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issue)).when(responseSpec).bodyToMono(Issue.class);

        Flux<CommentDetail> result = issueService.findIssueCommentsById(issue.getId(), 5, 1);

        result.subscribe(res -> assertThat(res).isEqualTo(commentDetail));
        verify(requestHeadersUriSpec, atLeastOnce()).uri("/issue/" + issue.getId());
        verify(responseSpec, atLeastOnce()).bodyToMono(Issue.class);
    }

    @Test
    void shouldFetchIssuesAndFilterWithLabel() {
        Issue emptyIssue = buildCommonIssue(1001, 1.0, "empty");
        Issue testIssue = buildCommonIssue(1001, 1.0, "test");
        Issues issues = buildIssuesByList(emptyIssue, testIssue);

        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issues)).when(responseSpec).bodyToMono(Issues.class);

        Flux<Issue> testResult = issueService.findIssuesByLabel(1, "test");
        Flux<Issue> allResult = issueService.findIssuesByLabel(1, " ");

        testResult.subscribe(testRes -> assertThat(testRes).isEqualTo(testIssue));
        allResult.subscribe(allRes -> assertThat(allRes).isEqualTo(emptyIssue));
        allResult.subscribe(allRes -> assertThat(allRes).isEqualTo(testIssue));
        verify(requestHeadersUriSpec, atLeastOnce()).uri("/board/1/issue");
        verify(responseSpec, atLeastOnce()).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssuesAndFilterWithDays() {
        Issue issueLastDay = buildCommonIssue(10001, 1.0, "");
        issueLastDay.getFields().setUpdated(Instant.now().minus(Period.ofDays(1)));
        Issue issueNow = buildCommonIssue(10001, 1.0, "");
        issueNow.getFields().setUpdated(Instant.now());
        Issues issues = buildIssuesByList(issueLastDay, issueNow);

        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issues)).when(responseSpec).bodyToMono(Issues.class);

        Flux<Issue> resultIn1Day = issueService.findRecentIssues(1, 1);
        Flux<Issue> resultIn2Days = issueService.findRecentIssues(1, 2);

        resultIn1Day.subscribe(res -> assertThat(res).isEqualTo(issueNow));
        resultIn2Days.subscribe(res -> assertThat(res).isEqualTo(issueNow));
        resultIn2Days.subscribe(res -> assertThat(res).isEqualTo(issueLastDay));
        verify(requestHeadersUriSpec, atLeastOnce()).uri("/board/1/issue");
        verify(responseSpec, atLeastOnce()).bodyToMono(Issues.class);
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
}
