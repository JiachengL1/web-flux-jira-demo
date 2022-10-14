package com.example.webfluxjirademo.domain.issue;

import com.example.webfluxjirademo.config.InstantFormatConfig;
import com.example.webfluxjirademo.domain.Project;
import com.example.webfluxjirademo.domain.User;
import com.example.webfluxjirademo.domain.comment.Comment;
import com.example.webfluxjirademo.domain.status.Status;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fields {

    private String summary;
    private String description;
    private boolean flagged;
    @JsonAlias("customfield_10016")
    private double storyPoint;
    @JsonFormat(pattern = InstantFormatConfig.PATTERN, timezone = InstantFormatConfig.TIMEZONE)
    private Instant lastViewed;
    @JsonFormat(pattern = InstantFormatConfig.PATTERN, timezone = InstantFormatConfig.TIMEZONE)
    private Instant created;
    @JsonFormat(pattern = InstantFormatConfig.PATTERN, timezone = InstantFormatConfig.TIMEZONE)
    private Instant updated;
    private User creator;
    private User reporter;
    private Project project;
    private Status status;
    private Comment comment;
    private List<String> labels;
}
