package com.github.toastshaman.tinytypes.validation;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static java.lang.String.join;

public class ValidationException extends RuntimeException {
    public final List<String> messages;

    public ValidationException(String name, List<String> messages) {
        super(format("%s: [%s]", name, join(", ", messages)));
        this.messages = Collections.unmodifiableList(messages);
    }
}
