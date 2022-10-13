package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.board.Board;
import com.example.webfluxjirademo.domain.board.Boards;
import com.example.webfluxjirademo.exception.BoardNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BoardService {

    private final WebClient webClient;

    public BoardService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Board> findAllBoards() {
        return webClient.get()
                .uri("/")
                .retrieve()
                .bodyToMono(Boards.class)
                .flatMapMany(boards -> Flux.fromIterable(boards.getValues()));
    }

    public Mono<Board> findBoardById(int id) {
        return webClient.get()
                .uri("/" + id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(BoardNotFoundException::new))
                .bodyToMono(Board.class);
    }
}
