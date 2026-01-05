// src/main/java/com/example/messaging/MessageEnvelope.java
package com.example.messaging;

import java.util.Map;

public class MessageEnvelope<T> {
    private final T payload;
    private final Map<String, String> headers;

    public MessageEnvelope(T payload, Map<String, String> headers) {
        this.payload = payload;
        this.headers = headers;
    }
    public T getPayload() { return payload; }
    public Map<String, String> getHeaders() { return headers; }

    public static final String HDR_TRACE_ID = "traceId";
    public static final String HDR_SERVICE_NAME = "serviceName";
    public static final String HDR_CONTENT_TYPE = "contentType";
}
