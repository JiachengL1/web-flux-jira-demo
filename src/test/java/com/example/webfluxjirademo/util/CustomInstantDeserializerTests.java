package com.example.webfluxjirademo.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class CustomInstantDeserializerTests {

    private ObjectMapper objectMapper;
    private CustomInstantDeserializer deserializer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        deserializer = new CustomInstantDeserializer();
    }

    @Test
    void shouldDeserializeStringToInstantWithFormat() throws IOException {
        LocalDateTime dateTime = LocalDateTime.of(2022, Month.OCTOBER, 19, 12, 30, 0);
        String dateTimeStr = "{\"dateTime\":\"2022-10-19T12:30:00.000+0800\"}";

        JsonParser parser = objectMapper.getFactory().createParser(dateTimeStr.getBytes(StandardCharsets.UTF_8));
        DeserializationContext context = objectMapper.getDeserializationContext();
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();

        Instant result = deserializer.deserialize(parser, context);
        assertThat(result).isEqualTo(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
