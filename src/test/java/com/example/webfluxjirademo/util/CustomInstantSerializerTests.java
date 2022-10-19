package com.example.webfluxjirademo.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class CustomInstantSerializerTests {

    private ObjectMapper objectMapper;
    private StringWriter stringWriter;
    private JsonFactory jsonFactory;
    private CustomInstantSerializer serializer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        stringWriter = new StringWriter();
        jsonFactory = new JsonFactory();
        serializer = new CustomInstantSerializer();
    }

    @Test
    void shouldSerializeInstantToStringWithFormat() throws IOException {
        LocalDateTime dateTime = LocalDateTime.of(2022, Month.OCTOBER, 19, 12, 30, 0);
        Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        String instantStr = "\"2022-10-19 12:30:00\"";

        JsonGenerator generator = jsonFactory.createGenerator(stringWriter);
        SerializerProvider provider = objectMapper.getSerializerProvider();

        serializer.serialize(instant, generator, provider);
        generator.flush();
        assertThat(stringWriter).hasToString(instantStr);
    }
}
