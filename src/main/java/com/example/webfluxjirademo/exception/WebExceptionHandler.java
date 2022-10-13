package com.example.webfluxjirademo.exception;

import com.example.webfluxjirademo.domain.board.Board;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(BoardNotFoundException.class)
    public Mono<Board> handleBoardNotFoundException() {
        return Mono.empty();
    }
}
