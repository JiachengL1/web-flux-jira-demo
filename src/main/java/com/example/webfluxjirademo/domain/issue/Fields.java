package com.example.webfluxjirademo.domain.issue;

import com.example.webfluxjirademo.config.CustomInstantDeserializer;
import com.example.webfluxjirademo.config.CustomInstantSerializer;
import com.example.webfluxjirademo.domain.Project;
import com.example.webfluxjirademo.domain.User;
import com.example.webfluxjirademo.domain.comment.Comment;
import com.example.webfluxjirademo.domain.status.Status;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    private Instant lastViewed;
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    private Instant created;
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    private Instant updated;
    private User creator;
    private User reporter;
    private Project project;
    private Status status;
    private Comment comment;
    private List<String> labels;
}
