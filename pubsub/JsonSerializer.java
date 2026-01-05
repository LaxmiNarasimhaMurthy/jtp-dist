// src/main/java/com/example/messaging/JsonSerializer.java
package com.example.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializer implements Serializer {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override public String contentType() { return "application/json"; }
    @Override public <T> byte[] serialize(T obj) throws Exception { return mapper.writeValueAsBytes(obj); }
    @Override public <T> T deserialize(byte[] bytes, Class<T> type) throws Exception { return mapper.readValue(bytes, type); }
}
