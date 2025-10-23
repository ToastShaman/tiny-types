package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.values.URIValue;
import java.net.URI;

public final class DeadLetterQueueUrl extends URIValue {

    public DeadLetterQueueUrl(URI value) {
        super(value);
    }

    public DeadLetterQueueUrl(String value) {
        super(URI.create(value));
    }

    public static DeadLetterQueueUrl parse(String value) {
        return new DeadLetterQueueUrl(URI.create(value));
    }
}
