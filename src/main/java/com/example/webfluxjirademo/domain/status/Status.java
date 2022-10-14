package com.example.webfluxjirademo.domain.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status {
    private int id;
    private String name;
    private String description;
    private String self;
    private String iconUrl;
    private StatusCategory statusCategory;
}
