package com.example.webfluxjirademo.domain.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    private int projectId;
    private String displayName;
    private String projectName;
    private String projectKey;
    private String projectTypeKey;
    private String avatarURI;
    private String name;
}
