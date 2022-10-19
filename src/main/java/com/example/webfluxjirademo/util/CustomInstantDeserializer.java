package com.example.webfluxjirademo.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.example.webfluxjirademo.util.InstantFormat.PATTERN;
import static com.example.webfluxjirademo.util.InstantFormat.TIMEZONE;

public class CustomInstantDeserializer extends StdDeserializer<Instant> {

    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern(PATTERN).withZone(ZoneId.of(TIMEZONE));

    public CustomInstantDeserializer() {
        this(null);
    }

    public CustomInstantDeserializer(Class<Instant> instant) {
        super(instant);
    }

    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return Instant.from(formatter.parse(parser.getText()));
    }
}
