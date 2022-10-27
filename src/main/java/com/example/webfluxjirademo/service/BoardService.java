package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.board.Board;
import com.example.webfluxjirademo.domain.board.Boards;
import com.example.webfluxjirademo.exception.BoardNotFoundException;
import com.example.webfluxjirademo.util.WebClientUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BoardService {

    private final WebClientUtil webClientUtil;

    public BoardService(WebClientUtil webClientUtil) {
        this.webClientUtil = webClientUtil;
    }

    public Flux<Board> findAllBoards() {
        return webClientUtil.getWebClient()
                .get().uri("/board")
                .retrieve()
                .bodyToMono(Boards.class)
                .flatMapMany(boards -> Flux.fromIterable(boards.getValues()));
    }

    public Mono<Board> findBoardById(int id) {
        return webClientUtil.getWebClient()
                .get().uri("/board/" + id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(BoardNotFoundException::new))
                .bodyToMono(Board.class);
    }
}
