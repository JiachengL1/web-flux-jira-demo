package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.issue.Issue;
import com.example.webfluxjirademo.domain.issue.Issues;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class IssueService {

    private final WebClient webClient;

    public IssueService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Issue> findAllIssues(int boardId) {
        return webClient.get()
                .uri(String.format("/board/%d/issue", boardId))
                .retrieve()
                .bodyToMono(Issues.class)
                .flatMapMany(issues -> Flux.fromIterable(issues.getIssues()));
    }
}
