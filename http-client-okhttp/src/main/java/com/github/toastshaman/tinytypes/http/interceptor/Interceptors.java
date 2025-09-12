package com.github.toastshaman.tinytypes.http.interceptor;

import com.github.toastshaman.tinytypes.events.Events;
import java.time.Clock;
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

    public static UnaryOperator<OkHttpClient.Builder> HttpEventRecordingInterceptor(Events events, Clock clock) {
        return builder -> builder.addInterceptor(new HttpEventRecordingInterceptor(events, clock));
    }
}
