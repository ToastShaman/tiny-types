package com.github.toastshaman.tinytypes.aws.sns;

import com.github.toastshaman.tinytypes.values.NonBlankStringValue;

public final class TopicArn extends NonBlankStringValue {

    public TopicArn(String value) {
        super(value);
    }

    public static TopicArn of(String value) {
        return new TopicArn(value);
    }
}
