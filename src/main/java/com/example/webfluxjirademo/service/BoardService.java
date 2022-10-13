package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.board.Board;
import com.example.webfluxjirademo.domain.board.Boards;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BoardService {

    private WebClient webClient;

    public BoardService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Board> findAllBoards() {
        Mono<Boards> boards = webClient
                .get()
                .uri("/")
                .retrieve()
                .bodyToMono(Boards.class);
        Flux<Board> boardsValue = boards.flatMapMany(e -> Flux.fromIterable(e.getValues()));
        return boardsValue;
    }
}
