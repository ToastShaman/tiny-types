package com.github.toastshaman.tinytypes.http;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class JsonPlaceholderHttpApiTest {

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

        JsonPlaceholderApi api = new JsonPlaceholderHttpApi(
                baseUrl, client, it -> it.addInterceptor(new HttpLoggingInterceptor().setLevel(BODY))
                        .addInterceptor(new ThrowIfUnsuccessful()));

        var getTodoWithId = new GetTodoWithId(TodoId.of(1));

        var result = api.execute(getTodoWithId);

        assertThat(result).isSuccess().hasValueSatisfying(todo -> {
            assertThat(todo.id()).isEqualTo(1);
            assertThat(todo.title()).isEqualTo("delectus aut autem");
            assertThat(todo.completed()).isFalse();
            assertThat(todo.userid()).isEqualTo(1);
        });

        server.close();
    }
}
