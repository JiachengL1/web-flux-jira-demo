package com.example.webfluxjirademo.controller;

import com.example.webfluxjirademo.domain.issue.Issue;
import com.example.webfluxjirademo.service.IssueService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/issue")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping
    public Flux<Issue> getAllIssues(@RequestParam("boardId") int boardId,
                                    @RequestParam("statusId") int statusId) {
        if (statusId != 0) {
            return issueService.findIssuesByStatus(boardId, statusId);
        }
        return issueService.findAllIssues(boardId);
    }
}
