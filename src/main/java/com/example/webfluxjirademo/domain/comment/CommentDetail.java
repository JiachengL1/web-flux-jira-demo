package com.example.webfluxjirademo.domain.comment;

import com.example.webfluxjirademo.config.InstantFormatConfig;
import com.example.webfluxjirademo.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDetail {

    private int id;
    private String self;
    private String body;
    private boolean jsdPublic;
    @JsonFormat(pattern = InstantFormatConfig.PATTERN, timezone = InstantFormatConfig.TIMEZONE)
    private Instant created;
    @JsonFormat(pattern = InstantFormatConfig.PATTERN, timezone = InstantFormatConfig.TIMEZONE)
    private Instant updated;
    private User author;
    private User updateAuthor;
}
