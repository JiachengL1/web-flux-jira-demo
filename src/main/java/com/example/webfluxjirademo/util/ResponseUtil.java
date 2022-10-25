package com.example.webfluxjirademo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ResponseUtil() {}

    public static Mono<Void> writeResponse (ServerHttpResponse response, Map<String, String> data) throws JsonProcessingException {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer dataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(data));
        return response.writeWith(Mono.just(dataBuffer));
    }
}
