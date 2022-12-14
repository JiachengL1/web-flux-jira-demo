package com.example.webfluxjirademo.domain.issue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Issue {

    private String expand;
    private int id;
    private String self;
    private String key;
    private Fields fields;
}
