package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.board.Board;
import com.example.webfluxjirademo.domain.board.Boards;
import com.example.webfluxjirademo.domain.board.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceTests {

    @InjectMocks
    private BoardService boardService;
    @Mock
    private WebClient webClient;
    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private RequestHeadersSpec requestHeadersSpec;
    @Mock
    private ResponseSpec responseSpec;

    @Test
    void shouldFetchBoardsDataFromJiraAndReturnValues() {
        Boards boards = new Boards(1, 1, 1, true, List.of(new Board()));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boards.class)).thenReturn(Mono.just(boards));

        Flux<Board> result = boardService.findAllBoards();

        StepVerifier.create(result)
                .expectNextMatches(new Board()::equals)
                .verifyComplete();
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/");
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Boards.class);
    }

    @Test
    void shouldFetchBoardDataByIdAndReturnWhole() {
        Board board = new Board(1, "/board/1", "board1", "simple", new Location());
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Board.class)).thenReturn(Mono.just(board));

        Mono<Board> result = boardService.findBoardById(board.getId());

        StepVerifier.create(result)
                .expectNextMatches(board::equals)
                .verifyComplete();
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/" + board.getId());
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Board.class);
    }
}
