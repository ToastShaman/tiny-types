package com.github.toastshaman.tinytypes.http;

import java.io.IOException;
import java.util.Objects;

public final class HttpResponseException extends IOException {

    private final int code;

    private final String body;

    public HttpResponseException(int code, String body) {
        super("HTTP request failed with: %d: %s".formatted(code, body));
        this.code = code;
        this.body = body;
    }

    public int code() {
        return code;
    }

    public String body() {
        return body;
    }

    public boolean isNotFound() {
        return 404 == code;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (HttpResponseException) obj;
        return this.code == that.code && Objects.equals(this.body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, body);
    }

    @Override
    public String toString() {
        return "HttpResponseException[code=%d, body=%s]".formatted(code, body);
    }
}
