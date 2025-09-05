package com.github.toastshaman.tinytypes.http;

import dev.failsafe.FailsafeExecutor;
import java.util.Objects;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public interface FailsafeHttpAction<R> extends HttpAction<R> {

    FailsafeExecutor<R> failsafe();

    static <R> FailsafeHttpActionBuilder<R> builder() {
        return new FailsafeHttpActionBuilder<>();
    }

    class FailsafeHttpActionBuilder<R> {
        private HttpAction<R> action;

        private FailsafeExecutor<R> executor;

        public FailsafeHttpActionBuilder() {}

        public FailsafeHttpActionBuilder<R> withAction(HttpAction<R> action) {
            this.action = action;
            return this;
        }

        public FailsafeHttpActionBuilder<R> withFailsafe(FailsafeExecutor<R> executor) {
            this.executor = executor;
            return this;
        }

        public FailsafeHttpAction<R> build() {
            Objects.requireNonNull(action);
            Objects.requireNonNull(executor);
            return FailsafeHttpAction.of(action, executor);
        }
    }

    static <R> FailsafeHttpAction<R> of(HttpAction<R> action, FailsafeExecutor<R> executor) {
        return new FailsafeHttpAction<>() {
            @Override
            public Request toRequest(HttpUrl.Builder builder) {
                return action.toRequest(builder);
            }

            @Override
            public R fromResponse(Response response) throws Exception {
                return action.fromResponse(response);
            }

            @Override
            public FailsafeExecutor<R> failsafe() {
                return executor;
            }
        };
    }
}
