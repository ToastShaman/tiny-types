package com.github.toastshaman.tinytypes.format.moshi;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ValueTypeAdapterFactory implements JsonAdapter.Factory {
    @Override
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        Class<?> rawType = Types.getRawType(type);
        Class<?> superclass = findValueType(rawType);
        if (superclass != null) {
            ParameterizedType parameterizedType = (ParameterizedType) superclass.getGenericSuperclass();
            Type typeParameter = parameterizedType.getActualTypeArguments()[0];
            JsonAdapter<Object> typeAdapter = moshi.adapter(typeParameter).nullSafe();
            return new ValueTypeAdapter(typeAdapter, rawType);
        }
        return null;
    }

    private Class<?> findValueType(Class<?> rawType) {
        Class<?> superclass = rawType.getSuperclass();
        if (superclass == null) return null;
        if (superclass.equals(AbstractValueType.class)) return rawType;
        return findValueType(superclass);
    }
}
