// src/main/java/com/example/messaging/Subscriber.java
package com.example.messaging;

import java.util.function.BiConsumer;

public interface Subscriber {
    <T> void subscribe(String subscription, Class<T> type, BiConsumer<T, MessageEnvelope<T>> handler);
}
