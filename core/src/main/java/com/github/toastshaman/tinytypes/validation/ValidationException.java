package com.github.toastshaman.tinytypes.validation;

import java.util.Collections;
import java.util.List;

public class ValidationException extends RuntimeException {
    public final List<String> messages;

    public ValidationException(String name, List<String> messages) {
        super(String.format("%s: [%s]", name, String.join(", ", messages)));
        this.messages = Collections.unmodifiableList(messages);
    }
}
