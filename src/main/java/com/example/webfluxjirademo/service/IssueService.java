package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.issue.Issue;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class IssueService {
    public Flux<Issue> findAllIssues(int boardId) {
        return null;
    }
}
