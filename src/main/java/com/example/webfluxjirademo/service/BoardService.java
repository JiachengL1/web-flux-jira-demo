package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.board.Board;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class BoardService {
    public Flux<Board> findAllBoards() {
        return null;
    }
}
