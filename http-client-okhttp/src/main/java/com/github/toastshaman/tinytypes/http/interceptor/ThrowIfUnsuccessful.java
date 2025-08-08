package com.github.toastshaman.tinytypes.http.interceptor;

import io.vavr.control.Try;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

public final class ThrowIfUnsuccessful implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        var request = chain.request();
        var response = chain.proceed(request);
        if (!response.isSuccessful()) {
            var body = response.peekBody(Long.MAX_VALUE);
            var json = Try.of(body::string).filter(it -> !it.isEmpty()).getOrElse("<EMPTY>");
            throw new HttpResponseException(response.code(), json);
        }
        return response;
    }
}
