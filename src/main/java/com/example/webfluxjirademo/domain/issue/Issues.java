package com.example.webfluxjirademo.domain.issue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Issues {

    private String expand;
    private int startAt;
    private int maxResults;
    private int total;
    private List<Issue> issues;
}
