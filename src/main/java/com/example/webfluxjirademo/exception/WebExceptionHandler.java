package com.example.webfluxjirademo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(BoardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<String> handleBoardNotFoundException(BoardNotFoundException exception) {
        String message = "Invalid board id!";
        log.error(message, exception);
        return Mono.just(message);
    }

    @ExceptionHandler(IssueNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<String> handleIssueNotFoundException(IssueNotFoundException exception) {
        String message = "Invalid issue id!";
        log.error(message, exception);
        return Mono.just(message);
    }
}
