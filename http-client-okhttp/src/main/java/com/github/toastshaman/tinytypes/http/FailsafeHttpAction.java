package com.github.toastshaman.tinytypes.http;

import dev.failsafe.FailsafeExecutor;

public interface FailsafeHttpAction<R> extends HttpAction<R> {

    FailsafeExecutor<R> failsafe();
}
