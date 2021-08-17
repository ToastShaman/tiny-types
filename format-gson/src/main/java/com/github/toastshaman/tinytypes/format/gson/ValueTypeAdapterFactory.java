package com.github.toastshaman.tinytypes.format.gson;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ValueTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = (Class<T>) type.getRawType();
        Class<? super T> superclass = findValueType(rawType);
        if (superclass != null) {
            ParameterizedType parameterizedType = (ParameterizedType) superclass.getGenericSuperclass();
            Type typeParameter = parameterizedType.getActualTypeArguments()[0];
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(typeParameter));
            return new ValueTypeAdapter(adapter, rawType);
        }
        return null;
    }

    private <T> Class<? super T> findValueType(Class<T> rawType) {
        Class<? super T> superclass = rawType.getSuperclass();
        if (superclass == null) return null;
        if (superclass.equals(AbstractValueType.class)) return rawType;
        return findValueType(superclass);
    }
}
