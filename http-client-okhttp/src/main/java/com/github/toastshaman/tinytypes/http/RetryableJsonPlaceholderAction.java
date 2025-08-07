package com.github.toastshaman.tinytypes.http;

import dev.failsafe.RetryPolicy;

public interface RetryableJsonPlaceholderAction<R> extends JsonPlaceholderAction<R> {

    RetryPolicy<R> retryPolicy();
}
