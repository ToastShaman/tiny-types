package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.values.NonBlankStringValue;

public final class QueueName extends NonBlankStringValue {

    public QueueName(String value) {
        super(value);
    }

    public static QueueName of(String value) {
        return new QueueName(value);
    }
}
