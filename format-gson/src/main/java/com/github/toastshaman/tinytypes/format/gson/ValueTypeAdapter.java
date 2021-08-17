package com.github.toastshaman.tinytypes.format.gson;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ValueTypeAdapter<R extends AbstractValueType<T>, T> extends TypeAdapter<R> {

    private final TypeAdapter<T> adapter;
    private final Class<R> rawType;

    public ValueTypeAdapter(TypeAdapter<T> adapter, Class<R> rawType) {
        this.adapter = adapter;
        this.rawType = rawType;
    }

    @Override
    public void write(JsonWriter out, R value) throws IOException {
        if (value == null) out.nullValue();
        else adapter.write(out, value.unwrap());
    }

    @Override
    public R read(JsonReader in) throws IOException {
        try {
            T read = adapter.read(in);
            return rawType.getConstructor(read.getClass()).newInstance(read);
        } catch (InvocationTargetException e) {
            throw new IOException(e.getTargetException());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}