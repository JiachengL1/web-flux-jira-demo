package com.example.webfluxjirademo.controller;

import com.example.webfluxjirademo.domain.board.Board;
import com.example.webfluxjirademo.service.BoardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public Flux<Board> getAllBoards() {
        return boardService.findAllBoards();
    }
}
