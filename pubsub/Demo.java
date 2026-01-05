@SpringBootApplication
public class DemoApp {

    private final Publisher publisher;
    private final Subscriber subscriber;

    public DemoApp(Publisher publisher, Subscriber subscriber) {
        this.publisher = publisher;
        this.subscriber = subscriber;
    }

    @PostConstruct
    public void init() {
        // Subscribe to messages
        subscriber.subscribe("orders-sub", OrderCreated.class, (payload, env) -> {
            System.out.println("TraceId=" + env.getHeaders().get("traceId"));
            System.out.println("Service=" + env.getHeaders().get("serviceName"));
            System.out.println("Payload=" + payload);
        });

        // Publish a message
        publisher.publish("orders-topic", new OrderCreated("123", 42.5));
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApp.class, args);
    }
}
