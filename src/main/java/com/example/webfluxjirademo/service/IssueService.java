package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.comment.CommentDetail;
import com.example.webfluxjirademo.domain.issue.Issue;
import com.example.webfluxjirademo.domain.issue.Issues;
import com.example.webfluxjirademo.exception.BoardNotFoundException;
import com.example.webfluxjirademo.exception.IssueNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.example.webfluxjirademo.util.Constants.DEFAULT_PAGE_NUM;
import static com.example.webfluxjirademo.util.Constants.DEFAULT_PAGE_SIZE;
import static com.example.webfluxjirademo.util.Constants.DEFAULT_STATUS_ID;
import static com.example.webfluxjirademo.util.Constants.DEFAULT_STORY_POINT;

@Service
public class IssueService {

    private final WebClient webClient;

    public IssueService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Issue> findAllIssues(int boardId, int statusId, double point) {
        return webClient.get()
                .uri(String.format("/board/%d/issue", boardId))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(BoardNotFoundException::new))
                .bodyToMono(Issues.class)
                .flatMapMany(issues -> Flux.fromIterable(issues.getIssues()))
                .filter(issue -> statusId == DEFAULT_STATUS_ID || issue.getFields().getStatus().getId() == statusId)
                .filter(issue -> point == DEFAULT_STORY_POINT || issue.getFields().getStoryPoint() == point);
    }

    public Mono<Issue> findIssueById(int id) {
        return webClient.get()
                .uri("/issue/" + id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(IssueNotFoundException::new))
                .bodyToMono(Issue.class);
    }

    public Flux<CommentDetail> findIssueCommentsById(int id, int pageSize, int pageNum) {
        pageSize = pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
        pageNum = pageNum > 0 ? pageNum : DEFAULT_PAGE_NUM;

        return findIssueById(id)
                .flatMapMany(issue -> Flux.fromIterable(issue.getFields().getComment().getComments()))
                .skip((long) (pageNum - 1) * pageSize)
                .take(pageSize);
    }

    public Flux<Issue> findIssuesByLabel(int boardId, String label) {
        return null;
    }
}
