package libraryapi.authservice.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
public class RabbitMQConfig {
    @Autowired
    private Environment env;

    public static final String AUTH_SYNC_EXCHANGE = "auth.sync.exchange";
    public static final String AUTH_ROUTING_KEY_SYNC = "auth.sync";

    public static final String AUTH_QUERY_EXCHANGE = "auth.query.exchange";
    public static final String AUTH_ROUTING_KEY_QUERY = "auth.query";

    @Bean
    public TopicExchange AuthSyncExchange() {
        return new TopicExchange(AUTH_SYNC_EXCHANGE);
    }

    @Bean
    public Queue AuthSyncQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("auth.sync.queue." + currentPort, true);
    }

    @Bean
    public Binding AuthSyncBinding(Queue AuthSyncQueue, TopicExchange AuthSyncExchange) {
        return BindingBuilder.bind(AuthSyncQueue).to(AuthSyncExchange).with(AUTH_ROUTING_KEY_SYNC);
    }

    // Data Auth query from other instances

    @Bean
    public DirectExchange AuthQueryExchange() {
        return new DirectExchange(AUTH_QUERY_EXCHANGE);
    }

    @Bean
    public Queue AuthQueryQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("auth.query.queue." + currentPort, true);
    }

    @Bean
    public Binding AuthQueryBinding(Queue AuthQueryQueue, DirectExchange AuthQueryExchange) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return BindingBuilder.bind(AuthQueryQueue).to(AuthQueryExchange).with(AUTH_ROUTING_KEY_QUERY + currentPort);
    }
}
