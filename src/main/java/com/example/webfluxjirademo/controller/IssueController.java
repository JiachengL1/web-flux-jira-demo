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

import static com.example.webfluxjirademo.util.Constants.DEFAULT_PAGE_NUM_STR;
import static com.example.webfluxjirademo.util.Constants.DEFAULT_PAGE_SIZE_STR;
import static com.example.webfluxjirademo.util.Constants.DEFAULT_STATUS_ID_STR;
import static com.example.webfluxjirademo.util.Constants.DEFAULT_STORY_POINT_STR;

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
            @RequestParam(value = "statusId", defaultValue = DEFAULT_STATUS_ID_STR) int statusId,
            @RequestParam(value = "point", defaultValue = DEFAULT_STORY_POINT_STR) double point) {
        return issueService.findAllIssues(boardId, statusId, point);
    }

    @GetMapping("/{id}")
    public Mono<Issue> getIssueById(@PathVariable("id") int id) {
        return issueService.findIssueById(id);
    }

    @GetMapping("/{id}/comments")
    public Flux<CommentDetail> getIssueCommentsById(
            @PathVariable("id") int id,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE_STR) int pageSize,
            @RequestParam(value = "pageNum", defaultValue = DEFAULT_PAGE_NUM_STR) int pageNum) {
        return issueService.findIssueCommentsById(id, pageSize, pageNum);
    }

    @GetMapping("/label")
    public Flux<Issue> getIssuesByLabel(@RequestParam("boardId") int boardId,
                                        @RequestParam(value = "label", defaultValue = "") String label) {
        return issueService.findIssuesByLabel(boardId, label);
    }
}
