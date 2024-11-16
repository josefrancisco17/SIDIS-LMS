package libraryapi.lendingservice.rabbitMQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
public class RabbitMQConfig {
    @Autowired
    private Environment env;

    public static final String LENDING_EXCHANGE = "lending.exchange";
    public static final String LENDING_ROUTING_KEY_SYNC = "lending.sync";

    @Bean
    public TopicExchange LendingSyncExchange() {
        return new TopicExchange(LENDING_EXCHANGE);
    }

    @Bean
    public Queue LendingSyncQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("lending.sync.queue." + currentPort, true);
    }

    @Bean
    public Binding LendingSyncBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(LENDING_ROUTING_KEY_SYNC);
    }
}
