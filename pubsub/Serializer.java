// src/main/java/com/example/messaging/Serializer.java
package com.example.messaging;

public interface Serializer {
    String contentType();
    <T> byte[] serialize(T obj) throws Exception;
    <T> T deserialize(byte[] bytes, Class<T> type) throws Exception;
}
