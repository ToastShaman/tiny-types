package com.github.toastshaman.tinytypes.http;

import io.vavr.control.Try;

public interface HttpApi {

    <R> Try<R> execute(HttpAction<R> action);
}
