package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.board.Board;
import com.example.webfluxjirademo.domain.board.Boards;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

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
}
