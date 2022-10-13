package com.example.webfluxjirademo.domain.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    private int id;
    private String self;
    private String name;
    private String type;
    private Location location;
}
