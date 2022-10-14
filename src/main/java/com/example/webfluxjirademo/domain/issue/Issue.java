package com.example.webfluxjirademo.domain.issue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Issue {

    private String expand;
    private int id;
    private String self;
    private String key;
    private String description;
    private String summary;
    private boolean flagged;
    private Instant lastViewed;
    private Instant created;
    private Instant updated;
//    private Fields fields;
//    private Project project;
//    private Status status;
//    private Comment comment;
}
