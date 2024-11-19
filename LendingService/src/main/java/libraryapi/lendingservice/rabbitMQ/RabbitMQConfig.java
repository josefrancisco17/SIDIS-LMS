package libraryapi.lendingservice.rabbitMQ;

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

    public static final String LENDING_SYNC_EXCHANGE = "lending.sync.exchange";
    public static final String LENDING_ROUTING_KEY_SYNC = "lending.sync";

    public static final String LENDING_QUERY_EXCHANGE = "lending.query.exchange";
    public static final String LENDING_ROUTING_KEY_QUERY = "lending.query";

    @Bean
    public TopicExchange LendingSyncExchange() {
        return new TopicExchange(LENDING_SYNC_EXCHANGE);
    }

    @Bean
    public Queue LendingSyncQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("lending.sync.queue." + currentPort, true);
    }

    @Bean
    public Binding LendingSyncBinding(Queue LendingSyncQueue, TopicExchange LendingSyncExchange) {
        return BindingBuilder.bind(LendingSyncQueue).to(LendingSyncExchange).with(LENDING_ROUTING_KEY_SYNC);
    }

    // Data Lending query from other instances

    @Bean
    public DirectExchange LendingQueryExchange() {
        return new DirectExchange(LENDING_QUERY_EXCHANGE);
    }

    @Bean
    public Queue LendingQueryQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("lending.query.queue." + currentPort, true);
    }

    @Bean
    public Binding LendingQueryBinding(Queue LendingQueryQueue, DirectExchange LendingQueryExchange) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return BindingBuilder.bind(LendingQueryQueue).to(LendingQueryExchange).with(LENDING_ROUTING_KEY_QUERY + currentPort);
    }
}
