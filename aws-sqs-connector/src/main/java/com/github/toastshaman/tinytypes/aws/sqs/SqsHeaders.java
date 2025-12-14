package com.github.toastshaman.tinytypes.aws.sqs;

public final class SqsHeaders {

    public static SqsHeader<String> TRACE_ID = SqsHeader.text("trace_id");

    public static SqsHeader<String> EVENT_ID = SqsHeader.text("event.id");
    public static SqsHeader<String> EVENT_TYPE = SqsHeader.text("event.type");
    public static SqsHeader<String> EVENT_VERSION = SqsHeader.text("event.version");
    public static SqsHeader<String> EVENT_SOURCE = SqsHeader.text("event.source");
    public static SqsHeader<String> EVENT_TIMESTAMP = SqsHeader.text("event.timestamp");

    private  SqsHeaders() {}
}
