package com.example.webfluxjirademo.domain.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusCategory {

    private int id;
    private String name;
    private String key;
    private String colorName;
    private String self;
}
