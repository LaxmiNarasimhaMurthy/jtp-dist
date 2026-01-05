// src/main/java/com/example/messaging/spring/MessagingAutoConfiguration.java
package com.example.messaging.spring;

import com.example.messaging.*;
import com.example.messaging.gcp.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingAutoConfiguration {

    @Bean public Serializer serializer() { return new JsonSerializer(); }

    @Bean
    public Tracing tracing(ServiceNameProperties props, TraceIdProviderImpl provider) {
        return new Tracing(props.getServiceName(), provider);
    }

    @Bean
    @ConditionalOnProperty(name="messaging.transport", havingValue="gcp")
    public Publisher gcpPublisher(MessagingProperties mp, Serializer s, Tracing t) {
        return new GcpPubSubPublisher(mp.getGcp().getProjectId(), s, t);
    }

    @Bean
    @ConditionalOnProperty(name="messaging.transport", havingValue="gcp")
    public Subscriber gcpSubscriber(MessagingProperties mp, Serializer s) {
        return new GcpPubSubSubscriber(mp.getGcp().getProjectId(), s);
    }
}
