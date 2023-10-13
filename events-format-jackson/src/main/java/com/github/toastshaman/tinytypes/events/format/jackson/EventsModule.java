package com.github.toastshaman.tinytypes.events.format.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.toastshaman.tinytypes.events.EventCategory;
import java.io.IOException;

public final class EventsModule extends SimpleModule {

    public EventsModule() {
        addSerializer(EventCategory.class, new ToStringSerializer());
        addDeserializer(EventCategory.class, new JsonDeserializer<>() {
            @Override
            public EventCategory deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return EventCategory.of(p.getText());
            }
        });
    }
}
