package com.github.toastshaman.tinytypes.http;

import io.vavr.control.Try;
import java.util.Objects;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public record GetTodoWithId(TodoId id) implements JsonPlaceholderAction<Todo> {

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
    public Try<Todo> fromResponse(Response response) {
        return Try.of(response::body)
                .mapTry(ResponseBody::string)
                .filter(Objects::nonNull)
                .map(Todo.Json::fromJson);
    }
}
