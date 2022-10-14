package com.example.webfluxjirademo.domain.comment;

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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Asia/Shanghai")
    private Instant created;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Asia/Shanghai")
    private Instant updated;
    private User author;
    private User updateAuthor;
}
