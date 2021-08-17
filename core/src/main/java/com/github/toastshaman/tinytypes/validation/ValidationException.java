package com.github.toastshaman.tinytypes.validation;

import static java.lang.String.join;

import java.util.List;

public final class ValidationException extends RuntimeException {
    public final List<String> messages;

    public ValidationException(String name, List<String> messages) {
        super("%s: [%s]".formatted(name, join(", ", messages)));
        this.messages = List.copyOf(messages);
    }
}
