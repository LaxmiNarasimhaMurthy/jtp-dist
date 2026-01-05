// src/main/java/com/example/messaging/gcp/GcpPubSubSubscriber.java
package com.example.messaging.gcp;

import com.example.messaging.*;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.AckReplyConsumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class GcpPubSubSubscriber implements Subscriber {
    private final String projectId;
    private final Serializer serializer;
    private final ConcurrentHashMap<String, com.google.cloud.pubsub.v1.Subscriber> subs = new ConcurrentHashMap<>();

    public GcpPubSubSubscriber(String projectId, Serializer serializer) {
        this.projectId = projectId;
        this.serializer = serializer;
    }

    @Override
    public <T> void subscribe(String subscription, Class<T> type, BiConsumer<T, MessageEnvelope<T>> handler) {
        ProjectSubscriptionName name = ProjectSubscriptionName.of(projectId, subscription);
        MessageReceiver receiver = (PubsubMessage message, AckReplyConsumer consumer) -> {
            try {
                T obj = serializer.deserialize(message.getData().toByteArray(), type);
                var headers = message.getAttributesMap();
                handler.accept(obj, new MessageEnvelope<>(obj, headers));
                consumer.ack();
            } catch (Exception e) {
                consumer.nack();
            }
        };
        com.google.cloud.pubsub.v1.Subscriber s = com.google.cloud.pubsub.v1.Subscriber.newBuilder(name, receiver).build();
        subs.put(subscription, s);
        s.startAsync().awaitRunning();
    }

    public void stop(String subscription) {
        var s = subs.remove(subscription);
        if (s != null) s.stopAsync().awaitTerminated();
    }
}
