package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.ParallelSqsMessageListenerThreadType.PLATFORM;
import static com.github.toastshaman.tinytypes.aws.sqs.ParallelSqsMessageListenerThreadType.VIRTUAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.toastshaman.tinytypes.aws.sqs.ParallelSqsMessageListener.ParallelPollingException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ParallelSqsMessageListenerTest {

    @Test
    void invokes_delegate_once_per_thread_and_sums_results() {
        var invocations = new AtomicInteger();

        SqsMessageListener delegate = () -> {
            invocations.incrementAndGet();
            return 2;
        };

        var options = new ParallelSqsMessageListenerOptions(4, PLATFORM);
        var listener = new ParallelSqsMessageListener(delegate, options);

        var processed = listener.poll();

        assertThat(processed).isEqualTo(8);
        assertThat(invocations).hasValue(4);
    }

    @Test
    void runs_polls_in_parallel_on_platform_threads() {
        var threads = 5;
        var latch = new CountDownLatch(threads);

        SqsMessageListener delegate = () -> {
            latch.countDown();
            try {
                // Block until all threads have entered, proving parallel execution.
                if (!latch.await(2, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Polls did not run in parallel");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            return 1;
        };

        var options = new ParallelSqsMessageListenerOptions(threads, PLATFORM);
        var listener = new ParallelSqsMessageListener(delegate, options);

        assertThat(listener.poll()).isEqualTo(threads);
    }

    @Test
    void runs_polls_in_parallel_on_virtual_threads() {
        SqsMessageListener delegate = () -> {
            assertThat(Thread.currentThread().isVirtual()).isTrue();
            return 1;
        };

        var options = new ParallelSqsMessageListenerOptions(3, VIRTUAL);
        var listener = new ParallelSqsMessageListener(delegate, options);

        assertThat(listener.poll()).isEqualTo(3);
    }

    @Test
    void uses_default_options_when_only_delegate_is_provided() {
        var invocations = new AtomicInteger();

        SqsMessageListener delegate = () -> {
            invocations.incrementAndGet();
            return 7;
        };

        var listener = new ParallelSqsMessageListener(delegate);

        assertThat(listener.poll()).isEqualTo(7);
        assertThat(invocations).hasValue(1);
    }

    @Test
    void waits_for_all_tasks_even_when_some_fail_and_aggregates_failures() {
        var invocations = new AtomicInteger();

        SqsMessageListener delegate = () -> {
            int call = invocations.incrementAndGet();
            if (call % 2 == 0) {
                throw new IllegalStateException("boom-%d".formatted(call));
            }
            return 1;
        };

        var options = new ParallelSqsMessageListenerOptions(4, PLATFORM);
        var listener = new ParallelSqsMessageListener(delegate, options);

        assertThatThrownBy(listener::poll)
                .isInstanceOf(ParallelPollingException.class)
                .satisfies(thrown -> {
                    var ex = (ParallelPollingException) thrown;
                    assertThat(ex.failures).hasSize(2);
                    assertThat(ex.getSuppressed()).hasSize(2);
                });

        assertThat(invocations).hasValue(4);
    }

    @Test
    void rejects_null_delegate() {
        assertThatThrownBy(() -> new ParallelSqsMessageListener(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("delegate");
    }

    @Test
    void rejects_null_options() {
        assertThatThrownBy(() -> new ParallelSqsMessageListener(() -> 0, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("options");
    }
}
