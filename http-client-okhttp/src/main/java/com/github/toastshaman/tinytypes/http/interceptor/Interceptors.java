package com.github.toastshaman.tinytypes.http.interceptor;

import java.util.function.UnaryOperator;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

public final class Interceptors {
    private Interceptors() {
        // Prevent instantiation
    }

    public static UnaryOperator<OkHttpClient.Builder> LoggingInterceptorWith(Level level) {
        return builder -> builder.addInterceptor(new HttpLoggingInterceptor().setLevel(level));
    }

    public static UnaryOperator<OkHttpClient.Builder> ThrowIfUnsuccessful() {
        return builder -> builder.addInterceptor(new ThrowIfUnsuccessful());
    }
}
