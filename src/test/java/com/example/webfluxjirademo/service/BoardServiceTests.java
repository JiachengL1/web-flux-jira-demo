package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.board.Board;
import com.example.webfluxjirademo.domain.board.Boards;
import com.example.webfluxjirademo.domain.board.Location;
import com.example.webfluxjirademo.exception.BoardNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BoardServiceTests {

    @InjectMocks
    private BoardService boardService;
    @Mock
    private WebClient webClient;
    @Mock
    private RequestHeadersUriSpec<?> requestHeadersUriSpec;
    @Mock
    private RequestHeadersSpec<?> requestHeadersSpec;
    @Mock
    private ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
    }

    @AfterEach
    void tearDown() {
        verify(webClient).get();
        verify(requestHeadersSpec).retrieve();
    }

    @Test
    void shouldFetchBoardsFromJiraAndReturnValues() {
        Boards boards = new Boards(1, 1, 1, true, List.of(new Board()));

        doReturn(Mono.just(boards)).when(responseSpec).bodyToMono(Boards.class);

        Flux<Board> result = boardService.findAllBoards();

        result.subscribe(res -> assertThat(res).isEqualTo(new Board()));
        verify(requestHeadersUriSpec).uri("/board");
        verify(responseSpec).bodyToMono(Boards.class);
    }

    @Test
    void shouldFetchBoardByIdAndReturnWhole() {
        Board board = new Board(1, "/board/1", "board1", "simple", new Location());

        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        doReturn(Mono.just(board)).when(responseSpec).bodyToMono(Board.class);

        Mono<Board> result = boardService.findBoardById(board.getId());

        result.subscribe(res -> assertThat(res).isEqualTo(board));
        verify(requestHeadersUriSpec).uri("/board/" + board.getId());
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(Board.class);
    }

    @Test
    void shouldFetchErrorByInvalidIdAndThrowException() {
        doThrow(BoardNotFoundException.class).when(responseSpec).onStatus(any(), any());

        Throwable throwable = catchThrowable(() -> boardService.findBoardById(1));

        assertThat(throwable).isExactlyInstanceOf(BoardNotFoundException.class);
        verify(requestHeadersUriSpec).uri("/board/1");
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec, never()).bodyToMono(Board.class);
    }
}
