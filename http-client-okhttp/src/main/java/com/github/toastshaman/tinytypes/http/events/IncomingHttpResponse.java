package com.github.toastshaman.tinytypes.http.events;

import com.github.toastshaman.tinytypes.events.Event;
import java.time.Duration;
import okhttp3.Request;
import okhttp3.Response;

public record IncomingHttpResponse(String method, String url, int status, Duration elapsed) implements Event {

    public static IncomingHttpResponse create(Request request, Response response, Duration elapsed) {
        var method = request.method();
        var url = request.url().toString();
        var status = response.code();
        return new IncomingHttpResponse(method, url, status, elapsed);
    }
}
