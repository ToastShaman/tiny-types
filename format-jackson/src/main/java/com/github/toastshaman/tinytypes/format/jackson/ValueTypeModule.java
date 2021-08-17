package com.github.toastshaman.tinytypes.format.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.toastshaman.tinytypes.AbstractValueType;

public class ValueTypeModule {
    public static <T extends AbstractValueType<?>> SimpleModule value(Class<T> clazz) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new AbstractValueTypeSerializer<>(clazz));
        return simpleModule;
    }
}
