package com.github.toastshaman.tinytypes.http.events;

import com.github.toastshaman.tinytypes.events.Event;
import okhttp3.Request;

public record OutgoingHttpRequest(String method, String url) implements Event {

    public static OutgoingHttpRequest from(Request request) {
        var method = request.method();
        var url = request.url().toString();
        return new OutgoingHttpRequest(method, url);
    }
}
