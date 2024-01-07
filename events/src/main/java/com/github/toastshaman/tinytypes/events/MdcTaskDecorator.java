package com.github.toastshaman.tinytypes.events;

import java.util.function.UnaryOperator;
import org.slf4j.MDC;

public final class MdcTaskDecorator implements UnaryOperator<Runnable> {

    @Override
    public Runnable apply(Runnable runnable) {
        return decorate(runnable);
    }

    public static Runnable decorate(Runnable command) {
        var copy = MDC.getCopyOfContextMap();

        return () -> {
            if (copy == null || copy.isEmpty()) {
                command.run();
                return;
            }

            try {
                MDC.setContextMap(copy);
                command.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
