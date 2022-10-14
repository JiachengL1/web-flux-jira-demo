package com.example.webfluxjirademo.service;

import com.example.webfluxjirademo.domain.board.Board;
import com.example.webfluxjirademo.domain.board.Boards;
import com.example.webfluxjirademo.domain.board.Location;
import com.example.webfluxjirademo.exception.BoardNotFoundException;
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
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
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
        basicMockWebClient();
        when(responseSpec.bodyToMono(Boards.class)).thenReturn(Mono.just(boards));

        Flux<Board> result = boardService.findAllBoards();

        StepVerifier.create(result)
                .expectNextMatches(new Board()::equals)
                .verifyComplete();
        basicVerifyWebClient("/board");
        verify(responseSpec).bodyToMono(Boards.class);
    }

    @Test
    void shouldFetchBoardDataByIdAndReturnWhole() {
        Board board = new Board(1, "/board/1", "board1", "simple", new Location());
        basicMockWebClient();
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Board.class)).thenReturn(Mono.just(board));

        Mono<Board> result = boardService.findBoardById(board.getId());

        StepVerifier.create(result)
                .expectNextMatches(board::equals)
                .verifyComplete();
        basicVerifyWebClient("/board/" + board.getId());
        verify(responseSpec).onStatus(any(Predicate.class), any(Function.class));
        verify(responseSpec).bodyToMono(Board.class);
    }

    @Test
    void shouldFetchErrorByInvalidIdAndThrowException() {
        basicMockWebClient();
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenThrow(BoardNotFoundException.class);

        Throwable throwable = catchThrowable(() -> boardService.findBoardById(1));

        assertThat(throwable).isExactlyInstanceOf(BoardNotFoundException.class);
        basicVerifyWebClient("/board/1");
        verify(responseSpec).onStatus(any(Predicate.class), any(Function.class));
        verify(responseSpec, never()).bodyToMono(Board.class);
    }

    private void basicMockWebClient() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    private void basicVerifyWebClient(String uri) {
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(uri);
        verify(requestHeadersSpec).retrieve();
    }
}
