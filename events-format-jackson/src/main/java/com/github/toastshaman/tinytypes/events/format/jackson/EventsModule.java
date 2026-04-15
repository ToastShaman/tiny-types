package com.github.toastshaman.tinytypes.events.format.jackson;

import com.github.toastshaman.tinytypes.events.EventCategory;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

public final class EventsModule extends SimpleModule {

    public EventsModule() {
        addSerializer(EventCategory.class, ToStringSerializer.instance);

        addDeserializer(EventCategory.class, new ValueDeserializer<>() {
            @Override
            public EventCategory deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
                return EventCategory.of(p.getString());
            }
        });
    }
}
