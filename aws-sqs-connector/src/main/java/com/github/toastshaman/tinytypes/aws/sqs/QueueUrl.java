package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.values.URIValue;
import java.net.URI;

public final class QueueUrl extends URIValue {

    public QueueUrl(URI value) {
        super(value);
    }

    public QueueUrl(String value) {
        super(URI.create(value));
    }

    public String asString() {
        return value.toString();
    }

    public static QueueUrl parse(String value) {
        return new QueueUrl(URI.create(value));
    }
}
