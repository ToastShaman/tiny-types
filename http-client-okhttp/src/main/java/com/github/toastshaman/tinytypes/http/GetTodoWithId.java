package com.github.toastshaman.tinytypes.http;

import dev.failsafe.RetryPolicy;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public record GetTodoWithId(TodoId id) implements RetryableJsonPlaceholderAction<Todo> {

    public GetTodoWithId {
        Objects.requireNonNull(id, "id must not be null");
    }

    @Override
    public Request toRequest(HttpUrl.Builder builder) {
        var url = builder.addPathSegment("todos")
                .addPathSegment(id.transform(String::valueOf))
                .build();

        return new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .get()
                .build();
    }

    @Override
    public Todo fromResponse(Response response) throws IOException {
        return Todo.Json.fromJson(response.body().string());
    }

    @Override
    public RetryPolicy<Todo> retryPolicy() {
        return RetryPolicy.<Todo>builder()
                .withMaxRetries(3)
                .withBackoff(Duration.ofMillis(100), Duration.ofMillis(500))
                .build();
    }
}
