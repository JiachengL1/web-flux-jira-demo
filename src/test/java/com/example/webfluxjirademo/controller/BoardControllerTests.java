package com.example.webfluxjirademo.controller;

import com.example.webfluxjirademo.domain.board.Board;
import com.example.webfluxjirademo.domain.board.Location;
import com.example.webfluxjirademo.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(BoardController.class)
class BoardControllerTests {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private BoardService boardService;

    @Test
    void shouldGetAllBoardsWhenRequestWithNoArgs() {
        Board board = new Board(1, "/board/1", "board1", "simple", new Location());
        when(boardService.findAllBoards()).thenReturn(Flux.just(board));

        webTestClient.get()
                .uri("http://localhost:8080/board")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Board.class)
                .hasSize(1)
                .contains(board);
        verify(boardService).findAllBoards();
    }
}