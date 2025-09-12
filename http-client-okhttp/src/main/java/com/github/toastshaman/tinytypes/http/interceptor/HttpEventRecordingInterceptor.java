package com.github.toastshaman.tinytypes.http.interceptor;

import com.github.toastshaman.tinytypes.events.Events;
import com.github.toastshaman.tinytypes.http.events.IncomingHttpResponse;
import com.github.toastshaman.tinytypes.http.events.OutgoingHttpRequest;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

public record HttpEventRecordingInterceptor(Events events, Clock clock) implements Interceptor {

    public HttpEventRecordingInterceptor {
        Objects.requireNonNull(events, "events must not be null");
        Objects.requireNonNull(clock, "clock must not be null");
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        var request = chain.request();

        events.record(OutgoingHttpRequest.from(request));

        var now = Instant.now(clock);

        var response = chain.proceed(request);

        var elapsed = Duration.between(now, Instant.now(clock));

        events.record(IncomingHttpResponse.create(request, response, elapsed));

        return response;
    }
}
