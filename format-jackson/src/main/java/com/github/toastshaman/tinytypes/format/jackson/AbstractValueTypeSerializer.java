package com.github.toastshaman.tinytypes.format.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.toastshaman.tinytypes.AbstractValueType;

import java.io.IOException;

public class AbstractValueTypeSerializer<T extends AbstractValueType<?>> extends StdSerializer<T> {
    protected AbstractValueTypeSerializer(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue(value.value, gen);
    }
}
