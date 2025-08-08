package com.github.toastshaman.tinytypes.http;

import static java.util.function.UnaryOperator.identity;

import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeExecutor;
import io.vavr.control.Try;
import java.util.Objects;
import java.util.function.Function;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class OkHttpApi implements HttpApi {

    private final HttpUrl base;

    private final OkHttpClient client;

    public OkHttpApi(HttpUrl base, OkHttpClient client) {
        this(base, client, identity());
    }

    public OkHttpApi(
            HttpUrl base, OkHttpClient client, Function<OkHttpClient.Builder, OkHttpClient.Builder> customizer) {
        Objects.requireNonNull(customizer, "Customizer function must not be null");
        Objects.requireNonNull(base, "Base URL must not be null");
        Objects.requireNonNull(client, "HTTP client must not be null");

        this.base = base;
        this.client = customizer.andThen(OkHttpClient.Builder::build).apply(client.newBuilder());
    }

    @Override
    public <R> Try<R> execute(HttpAction<R> action) {
        FailsafeExecutor<R> executor = action instanceof FailsafeHttpAction<R> actionWithFailsafe
                ? actionWithFailsafe.failsafe()
                : Failsafe.none();

        return Try.of(() -> executor.get(() -> {
            var request = action.toRequest(base.newBuilder());
            try (var response = client.newCall(request).execute()) {
                return action.fromResponse(response);
            }
        }));
    }
}
