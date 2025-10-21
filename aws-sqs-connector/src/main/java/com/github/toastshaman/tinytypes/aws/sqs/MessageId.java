package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.values.NonBlankStringValue;

public final class MessageId extends NonBlankStringValue {

    public MessageId(String value) {
        super(value);
    }

    public static MessageId of(String value) {
        return new MessageId(value);
    }
}
