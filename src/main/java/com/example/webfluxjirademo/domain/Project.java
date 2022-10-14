package com.example.webfluxjirademo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    private int id;
    private String self;
    private String key;
    private String name;
    private String projectTypeKey;
}
