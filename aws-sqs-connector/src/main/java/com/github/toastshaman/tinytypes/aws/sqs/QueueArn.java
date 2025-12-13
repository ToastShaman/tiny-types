package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.values.NonBlankStringValue;

public final class QueueArn extends NonBlankStringValue {

    public QueueArn(String value) {
        super(value);
    }

    public static QueueArn of(String value) {
        return new QueueArn(value);
    }
}
