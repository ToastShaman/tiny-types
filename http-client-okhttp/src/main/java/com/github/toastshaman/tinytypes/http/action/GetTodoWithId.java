package com.github.toastshaman.tinytypes.http.action;

import com.github.toastshaman.tinytypes.http.FailsafeHttpAction;
import com.github.toastshaman.tinytypes.http.domain.Todo;
import com.github.toastshaman.tinytypes.http.domain.TodoId;
import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeExecutor;
import dev.failsafe.RetryPolicy;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public record GetTodoWithId(TodoId id) implements FailsafeHttpAction<Todo> {

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
    public FailsafeExecutor<Todo> failsafe() {
        var retry = RetryPolicy.<Todo>builder()
                .withMaxRetries(3)
                .withBackoff(Duration.ofMillis(100), Duration.ofMillis(500))
                .build();

        return Failsafe.with(retry);
    }
}
