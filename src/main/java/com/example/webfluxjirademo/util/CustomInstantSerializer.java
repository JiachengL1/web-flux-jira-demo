package com.example.webfluxjirademo.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.example.webfluxjirademo.util.InstantFormat.SIMPLE_PATTERN;

public class CustomInstantSerializer extends StdSerializer<Instant> {

    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern(SIMPLE_PATTERN).withZone(ZoneId.systemDefault());

    public CustomInstantSerializer() {
        this(null);
    }

    public CustomInstantSerializer(Class<Instant> t) {
        super(t);
    }

    @Override
    public void serialize(Instant value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(formatter.format(value));
    }
}
