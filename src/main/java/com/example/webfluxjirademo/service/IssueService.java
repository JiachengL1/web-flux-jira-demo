package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.comment.CommentDetail;
import com.example.webfluxjirademo.domain.issue.Issue;
import com.example.webfluxjirademo.domain.issue.Issues;
import com.example.webfluxjirademo.exception.BoardNotFoundException;
import com.example.webfluxjirademo.exception.IssueNotFoundException;
import com.example.webfluxjirademo.util.WebClientUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.Period;

import static com.example.webfluxjirademo.util.Constants.DEFAULT_STATUS_ID;
import static com.example.webfluxjirademo.util.Constants.DEFAULT_STORY_POINT;

@Service
public class IssueService {

    private final WebClientUtil webClientUtil;

    public IssueService(WebClientUtil webClientUtil) {
        this.webClientUtil = webClientUtil;
    }

    public Flux<Issue> findAllIssues(int boardId, int statusId, double point) {
        return webClientUtil.getWebClient()
                .get().uri(String.format("/board/%d/issue", boardId))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(BoardNotFoundException::new))
                .bodyToMono(Issues.class)
                .flatMapMany(issues -> Flux.fromIterable(issues.getIssues()))
                .filter(issue -> statusId == DEFAULT_STATUS_ID || issue.getFields().getStatus().getId() == statusId)
                .filter(issue -> point == DEFAULT_STORY_POINT || issue.getFields().getStoryPoint() == point);
    }

    public Mono<Issue> findIssueById(int id) {
        return webClientUtil.getWebClient()
                .get().uri("/issue/" + id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(IssueNotFoundException::new))
                .bodyToMono(Issue.class);
    }

    public Flux<CommentDetail> findIssueCommentsById(int id, int pageSize, int pageNum) {
        return findIssueById(id)
                .flatMapMany(issue -> Flux.fromIterable(issue.getFields().getComment().getComments()))
                .skip((long) (pageNum - 1) * pageSize)
                .take(pageSize);
    }

    public Flux<Issue> findIssuesByLabel(int boardId, String label) {
        return findAllIssues(boardId, DEFAULT_STATUS_ID, DEFAULT_STORY_POINT)
                .filter(issue -> label.isBlank() || issue.getFields().getLabels().contains(label));
    }

    public Flux<Issue> findRecentIssues(int boardId, int days) {
        Instant baseline = Instant.now().minus(Period.ofDays(days));
        return findAllIssues(boardId, -1, -1)
                .filter(issue -> issue.getFields().getUpdated().isAfter(baseline))
                .sort((o1, o2) -> o2.getFields().getUpdated().compareTo(o1.getFields().getUpdated()));
    }
}
