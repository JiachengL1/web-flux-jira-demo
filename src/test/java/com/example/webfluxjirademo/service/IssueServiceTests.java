package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.issue.Fields;
import com.example.webfluxjirademo.domain.issue.Issue;
import com.example.webfluxjirademo.domain.issue.Issues;
import com.example.webfluxjirademo.domain.status.Status;
import com.example.webfluxjirademo.exception.BoardNotFoundException;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

        Flux<Issue> result = issueService.findAllIssues(1);

        StepVerifier.create(result)
                .expectNextMatches(new Issue()::equals)
                .verifyComplete();
        basicVerifyWebClient("/board/1/issue");
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssuesAndReturnFilteredIssueFluxByStatusId() {
        Issue issue1 = buildIssueByStatus(10001);
        Issue issue2 = buildIssueByStatus(10002);
        Issues issues = new Issues("issues", 0, 10, 1, List.of(issue1, issue2));

        basicMockWebClient();
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(issues)).when(responseSpec).bodyToMono(Issues.class);

        Flux<Issue> result = issueService.findIssuesByStatus(1, 10001);

        StepVerifier.create(result)
                .expectNextMatches(issue1::equals)
                .verifyComplete();
        basicVerifyWebClient("/board/1/issue");
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchErrorByInvalidBoardIdAndThrowException() {
        basicMockWebClient();
        doThrow(BoardNotFoundException.class).when(responseSpec).onStatus(any(), any());

        Throwable allThrowable = catchThrowable(() -> issueService.findAllIssues(1));
        Throwable statusThrowable = catchThrowable(() -> issueService.findIssuesByStatus(1, 10001));

        assertThat(allThrowable).isExactlyInstanceOf(BoardNotFoundException.class);
        assertThat(statusThrowable).isExactlyInstanceOf(BoardNotFoundException.class);
        verify(responseSpec, times(2)).onStatus(any(), any());
        verify(responseSpec, never()).bodyToMono(Issues.class);
    }

    @Test
    void shouldFetchIssueAndReturnWhole() {
        Issue issue = buildIssueByStatus(10001);

        basicMockWebClient();
        doReturn(Mono.just(issue)).when(responseSpec).bodyToMono(Issue.class);

        Mono<Issue> result = issueService.findIssueById(issue.getId());

        StepVerifier.create(result)
                .expectNextMatches(issue::equals)
                .verifyComplete();
        basicVerifyWebClient("/issue/" + issue.getId());
        verify(responseSpec).bodyToMono(Issue.class);
    }

    private Issue buildIssueByStatus(int statusId) {
        Status status = new Status();
        status.setId(statusId);
        Fields fields = Fields.builder().status(status).build();
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
