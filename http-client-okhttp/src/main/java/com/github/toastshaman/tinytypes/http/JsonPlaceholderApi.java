package com.github.toastshaman.tinytypes.http;

import io.vavr.control.Try;

public interface JsonPlaceholderApi {

    <R> Try<R> execute(JsonPlaceholderAction<R> action);
}
