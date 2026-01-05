// src/main/java/com/example/messaging/gcp/GcpPubSubPublisher.java
package com.example.messaging.gcp;

import com.example.messaging.*;
import com.google.cloud.pubsub.v1.Publisher.Builder;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class GcpPubSubPublisher implements Publisher {
    private final String projectId;
    private final Serializer serializer;
    private final Tracing tracing;
    private final ConcurrentHashMap<String, Publisher> cache = new ConcurrentHashMap<>();

    public GcpPubSubPublisher(String projectId, Serializer serializer, Tracing tracing) {
        this.projectId = projectId;
        this.serializer = serializer;
        this.tracing = tracing;
    }

    @Override
    public <T> void publish(String topic, T payload) {
        publish(topic, new MessageEnvelope<>(payload, tracing.standardHeaders(serializer.contentType())));
    }

    @Override
    public <T> void publish(String topic, MessageEnvelope<T> envelope) {
        try {
            byte[] bytes = serializer.serialize(envelope.getPayload());
            PubsubMessage.Builder msgBuilder = PubsubMessage.newBuilder()
                .setData(com.google.protobuf.ByteString.copyFrom(bytes));
            envelope.getHeaders().forEach(msgBuilder::putAttributes);

            Publisher pub = cache.computeIfAbsent(topic, t -> {
                try {
                    return new Builder(ProjectTopicName.of(projectId, t)).build();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            pub.publish(msgBuilder.build());
        } catch (Exception e) {
            throw new RuntimeException("Publish failed", e);
        }
    }

    public void shutdown() {
        cache.values().forEach(p -> {
            try { p.shutdown(); p.awaitTermination(5, TimeUnit.SECONDS); } catch (Exception ignored) {}
        });
        cache.clear();
    }
}
