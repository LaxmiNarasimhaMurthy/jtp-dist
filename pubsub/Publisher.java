// src/main/java/com/example/messaging/Publisher.java
package com.example.messaging;

public interface Publisher {
    <T> void publish(String topic, T payload);
    <T> void publish(String topic, MessageEnvelope<T> envelope);
}
