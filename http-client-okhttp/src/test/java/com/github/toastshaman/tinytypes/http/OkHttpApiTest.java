package com.github.toastshaman.tinytypes.http;

import static com.github.toastshaman.tinytypes.http.interceptor.Interceptors.LoggingInterceptorWith;
import static com.github.toastshaman.tinytypes.http.interceptor.Interceptors.ThrowIfUnsuccessful;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

import com.github.toastshaman.tinytypes.http.action.GetTodoWithId;
import com.github.toastshaman.tinytypes.http.domain.Todo;
import com.github.toastshaman.tinytypes.http.domain.TodoId;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import java.io.IOException;
import java.time.Duration;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class OkHttpApiTest {

    @Test
    void can_retrieve_todo_from_external_api() throws IOException {
        var client = new OkHttpClient();

        var server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(
                        """
                        {
                          "userId": 1,
                          "id": 1,
                          "title": "delectus aut autem",
                          "completed": false
                        }""")
                .setResponseCode(200));
        server.start();

        var baseUrl = server.url("/");

        var api = new OkHttpApi(baseUrl, client, LoggingInterceptorWith(BODY).andThen(ThrowIfUnsuccessful()));

        var action = new GetTodoWithId(TodoId.of(1));

        var result = api.execute(action);

        assertThat(result).isSuccess().hasValueSatisfying(todo -> {
            assertThat(todo.id()).isEqualTo(1);
            assertThat(todo.title()).isEqualTo("delectus aut autem");
            assertThat(todo.completed()).isFalse();
            assertThat(todo.userid()).isEqualTo(1);
        });

        server.close();
    }

    @Test
    void can_retrieve_todo_from_external_api_with_retry() throws IOException {
        var client = new OkHttpClient();

        var server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(500));
        server.enqueue(new MockResponse().setResponseCode(500));
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(
                        """
                        {
                          "userId": 1,
                          "id": 1,
                          "title": "delectus aut autem",
                          "completed": false
                        }""")
                .setResponseCode(200));
        server.start();

        var baseUrl = server.url("/");

        var api = new OkHttpApi(baseUrl, client, LoggingInterceptorWith(BODY).andThen(ThrowIfUnsuccessful()));

        var action = new GetTodoWithId(TodoId.of(1));

        var executor = Failsafe.with(RetryPolicy.<Todo>builder()
                .withMaxRetries(3)
                .withBackoff(Duration.ofMillis(100), Duration.ofMillis(500))
                .build());

        var actionWithRetry = FailsafeHttpAction.<Todo>builder()
                .withAction(action)
                .withFailsafe(executor)
                .build();

        var result = api.execute(actionWithRetry);

        assertThat(result).isSuccess().hasValueSatisfying(todo -> {
            assertThat(todo.id()).isEqualTo(1);
            assertThat(todo.title()).isEqualTo("delectus aut autem");
            assertThat(todo.completed()).isFalse();
            assertThat(todo.userid()).isEqualTo(1);
        });

        server.close();
    }
}
