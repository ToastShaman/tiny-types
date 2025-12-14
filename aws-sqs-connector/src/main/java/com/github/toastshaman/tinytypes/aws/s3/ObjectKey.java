package com.github.toastshaman.tinytypes.aws.s3;

import java.util.List;
import java.util.stream.Stream;

public record ObjectKey(List<String> parts) {

    public ObjectKey {
        if (parts == null || parts.isEmpty()) {
            throw new IllegalArgumentException("ObjectKey parts cannot be null or empty");
        }

        for (String part : parts) {
            if (part == null || part.isBlank() || part.contains("/")) {
                throw new IllegalArgumentException("ObjectKey parts cannot contain null or blank strings");
            }
        }
    }

    public ObjectKey append(String part) {
        return new ObjectKey(Stream.concat(parts.stream(), Stream.of(part)).toList());
    }

    public String asString() {
        return String.join("/", parts);
    }

    public static ObjectKey of(String... parts) {
        return new ObjectKey(List.of(parts));
    }
}
