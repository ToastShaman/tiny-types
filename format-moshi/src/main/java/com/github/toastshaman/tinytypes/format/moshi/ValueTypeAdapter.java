package com.github.toastshaman.tinytypes.format.moshi;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ValueTypeAdapter<R extends AbstractValueType<T>, T> extends JsonAdapter<R> {

    private final JsonAdapter<T> typeAdapter;
    private final Class<R> rawType;

    public ValueTypeAdapter(JsonAdapter<T> typeAdapter, Class<R> rawType) {
        this.typeAdapter = typeAdapter;
        this.rawType = rawType;
    }

    @Override
    public R fromJson(JsonReader reader) throws IOException {
        try {
            T read = typeAdapter.fromJson(reader);
            if (read == null) return null;
            else return rawType.getConstructor(read.getClass()).newInstance(read);
        } catch (InvocationTargetException e) {
            throw new IOException(e.getTargetException());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void toJson(JsonWriter writer, R value) throws IOException {
        if (value == null) writer.nullValue();
        else typeAdapter.toJson(writer, value.unwrap());
    }
}
