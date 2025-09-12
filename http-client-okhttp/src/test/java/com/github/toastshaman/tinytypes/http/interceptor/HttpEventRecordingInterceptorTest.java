package com.github.toastshaman.tinytypes.http.interceptor;

import static com.github.toastshaman.tinytypes.events.test.assertions.RecordingEventsAssertions.assertThatEvents;
import static com.github.toastshaman.tinytypes.http.interceptor.Interceptors.HttpEventRecordingInterceptor;
import static com.github.toastshaman.tinytypes.http.interceptor.Interceptors.LoggingInterceptorWith;
import static com.github.toastshaman.tinytypes.http.interceptor.Interceptors.ThrowIfUnsuccessful;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.toastshaman.tinytypes.events.PrintStreamEventLogger;
import com.github.toastshaman.tinytypes.events.RecordingEvents;
import com.github.toastshaman.tinytypes.http.OkHttpApi;
import com.github.toastshaman.tinytypes.http.action.GetTodoWithId;
import com.github.toastshaman.tinytypes.http.domain.TodoId;
import com.github.toastshaman.tinytypes.http.events.IncomingHttpResponse;
import com.github.toastshaman.tinytypes.http.events.OutgoingHttpRequest;
import java.io.IOException;
import java.time.Clock;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class HttpEventRecordingInterceptorTest {

    @Test
    void emits_events_when_requests_are_made() throws IOException {
        var recordingEvents = new RecordingEvents();
        var events = recordingEvents.and(new PrintStreamEventLogger(System.out));
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

        var interceptorChain = LoggingInterceptorWith(BODY)
                .andThen(HttpEventRecordingInterceptor(events, Clock.systemUTC()))
                .andThen(ThrowIfUnsuccessful());

        var api = new OkHttpApi(baseUrl, client, interceptorChain);

        var action = new GetTodoWithId(TodoId.of(1));

        try {
            api.execute(action);
        } finally {
            server.close();
        }

        assertThatEvents(recordingEvents).hasEventSatisfying(OutgoingHttpRequest.class, e -> {
            var event = (OutgoingHttpRequest) e.getFirst();
            assertThat(event.method()).isEqualTo("GET");
            assertThat(event.url()).endsWith("/todos/1");
        });

        assertThatEvents(recordingEvents).hasEventSatisfying(IncomingHttpResponse.class, e -> {
            var event = (IncomingHttpResponse) e.getFirst();
            assertThat(event.method()).isEqualTo("GET");
            assertThat(event.url()).endsWith("/todos/1");
            assertThat(event.status()).isEqualTo(200);
            assertThat(event.elapsed()).isPositive();
        });
    }
}
