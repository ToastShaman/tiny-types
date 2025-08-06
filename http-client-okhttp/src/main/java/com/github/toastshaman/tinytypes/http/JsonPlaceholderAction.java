package com.github.toastshaman.tinytypes.http;

import io.vavr.control.Try;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public interface JsonPlaceholderAction<R> {

    Request toRequest(HttpUrl.Builder builder);

    Try<R> fromResponse(Response response);
}
