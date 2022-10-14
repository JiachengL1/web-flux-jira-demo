package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.issue.Fields;
import com.example.webfluxjirademo.domain.issue.Issue;
import com.example.webfluxjirademo.domain.issue.Issues;
import com.example.webfluxjirademo.domain.status.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueServiceTests {

    @InjectMocks
    private IssueService issueService;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Test
    void shouldFetchIssuesByBoardIdAndReturnIssueFlux() {
        Issues issues = new Issues("issues", 0, 10, 1, List.of(new Issue()));
        basicMockWebClient(issues);

        Flux<Issue> result = issueService.findAllIssues(1);

        StepVerifier.create(result)
                .expectNextMatches(new Issue()::equals)
                .verifyComplete();
        basicVerifyWebClient();
    }

    @Test
    void shouldFetchIssuesAndReturnFilteredIssueFluxByStatusId() {
        Issue issue1 = buildIssueByStatus(10001);
        Issue issue2 = buildIssueByStatus(10002);
        Issues issues = new Issues("issues", 0, 10, 1, List.of(issue1, issue2));

        basicMockWebClient(issues);

        Flux<Issue> result = issueService.findIssuesByStatus(1, 10001);

        StepVerifier.create(result)
                .expectNextMatches(issue1::equals)
                .verifyComplete();
        basicVerifyWebClient();
    }

    private Issue buildIssueByStatus(int statusId) {
        Status status = new Status();
        status.setId(statusId);
        Fields fields = Fields.builder().status(status).build();
        return new Issue("issue", 1, "issue1", "web", fields);
    }

    private void basicMockWebClient(Issues issues) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Issues.class)).thenReturn(Mono.just(issues));
    }

    private void basicVerifyWebClient() {
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/board/1/issue");
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Issues.class);
    }
}
