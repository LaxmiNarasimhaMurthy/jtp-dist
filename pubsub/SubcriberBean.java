import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class PubSubSubscriberBean {

    private final Subscriber subscriber;

    public PubSubSubscriberBean() {
        ProjectSubscriptionName subName = ProjectSubscriptionName.of("my-project", "orders-sub");
        this.subscriber = Subscriber.newBuilder(subName, (message, consumer) -> {
            System.out.println("Received: " + message.getData().toStringUtf8());
            consumer.ack();
        }).build();
        subscriber.startAsync().awaitRunning();
    }

    @PreDestroy
    public void stop() {
        subscriber.stopAsync().awaitTerminated();
        System.out.println("✅ Pub/Sub subscriber stopped cleanly");
    }
}
