package com.example.webfluxjirademo.domain.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Boards {
    private int maxResult;
    private int startAt;
    private int total;
    private boolean isLast;
    private List<Board> values;
}
