package com.example.webfluxjirademo.domain.comment;

import com.example.webfluxjirademo.util.CustomInstantDeserializer;
import com.example.webfluxjirademo.util.CustomInstantSerializer;
import com.example.webfluxjirademo.domain.User;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    private Instant created;
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    private Instant updated;
    private User author;
    private User updateAuthor;
}
