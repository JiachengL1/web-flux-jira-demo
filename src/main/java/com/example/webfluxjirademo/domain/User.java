package com.example.webfluxjirademo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String self;
    private String accountId;
    private String emailAddress;
    private String displayName;
    private boolean active;
    private String timeZone;
    private String accountType;
}
