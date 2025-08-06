package com.github.toastshaman.tinytypes.http;

import io.vavr.control.Try;
import java.util.Objects;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public record GetTodoWithId(int requestedId) implements JsonPlaceholderAction<Todo> {

    public GetTodoWithId {
        if (requestedId <= 0) {
            throw new IllegalArgumentException("Requested ID must be a positive integer");
        }
    }

    @Override
    public Request toRequest(HttpUrl.Builder builder) {
        var url = builder.addPathSegment("todos")
                .addPathSegment(String.valueOf(requestedId))
                .build();

        return new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .get()
                .build();
    }

    @Override
    public Try<Todo> fromResponse(Response response) {
        return Try.of(response::body)
                .mapTry(ResponseBody::string)
                .filter(Objects::nonNull)
                .map(Todo.Json::fromJson);
    }
}
