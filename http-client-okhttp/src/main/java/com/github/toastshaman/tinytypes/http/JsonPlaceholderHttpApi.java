package com.github.toastshaman.tinytypes.http;

import static io.vavr.Function1.identity;

import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeExecutor;
import io.vavr.Function1;
import io.vavr.control.Try;
import java.util.Objects;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public final class JsonPlaceholderHttpApi implements JsonPlaceholderApi {

    private final HttpUrl base;

    private final OkHttpClient client;

    public JsonPlaceholderHttpApi(HttpUrl base, OkHttpClient client) {
        this(base, client, identity());
    }

    public JsonPlaceholderHttpApi(
            HttpUrl base, OkHttpClient client, Function1<OkHttpClient.Builder, OkHttpClient.Builder> customizer) {
        Objects.requireNonNull(customizer, "Customizer function must not be null");
        Objects.requireNonNull(base, "Base URL must not be null");
        Objects.requireNonNull(client, "HTTP client must not be null");

        this.base = base;
        this.client = customizer.andThen(OkHttpClient.Builder::build).apply(client.newBuilder());
    }

    @Override
    public <R> Try<R> execute(JsonPlaceholderAction<R> action) {
        FailsafeExecutor<R> executor = action instanceof RetryableJsonPlaceholderAction<R> actionWithRetry
                ? Failsafe.with(actionWithRetry.retryPolicy())
                : Failsafe.none();

        return Try.of(() -> executor.get(() -> {
            var request = action.toRequest(base.newBuilder());
            try (var response = client.newCall(request).execute()) {
                return action.fromResponse(response);
            }
        }));
    }
}
