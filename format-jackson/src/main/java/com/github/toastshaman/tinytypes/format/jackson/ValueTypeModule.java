package com.github.toastshaman.tinytypes.format.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.toastshaman.tinytypes.AbstractValueType;

import java.io.IOException;

public class ValueTypeModule extends SimpleModule {

    public ValueTypeModule() {
        addSerializer(AbstractValueType.class, new JsonSerializer<>() {
            @Override
            public void serialize(AbstractValueType value,
                                  JsonGenerator gen,
                                  SerializerProvider serializers) throws IOException {
                gen.getCodec().writeValue(gen, value.unwrap());
            }
        });
    }
}
