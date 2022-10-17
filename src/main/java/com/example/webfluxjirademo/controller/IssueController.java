package com.example.webfluxjirademo.controller;

import com.example.webfluxjirademo.domain.comment.CommentDetail;
import com.example.webfluxjirademo.domain.issue.Issue;
import com.example.webfluxjirademo.service.IssueService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/issue")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping
    public Flux<Issue> getAllIssues(
            @RequestParam("boardId") int boardId,
            @RequestParam(value = "statusId", defaultValue = "-1") int statusId) {
        if (statusId != -1) {
            return issueService.findIssuesByStatus(boardId, statusId);
        }
        return issueService.findAllIssues(boardId);
    }

    @GetMapping("/{id}")
    public Mono<Issue> getIssueById(@PathVariable("id") int id) {
        return issueService.findIssueById(id);
    }

    @GetMapping("/{id}/comments")
    public Flux<CommentDetail> getIssueCommentsById(
            @PathVariable("id") int id,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum) {
        return issueService.findIssueCommentsById(id, pageSize, pageNum);
    }
}
