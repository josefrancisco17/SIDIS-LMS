package libraryapi.authservice.rabbitMQ;

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

    public static final String AUTH_EXCHANGE = "auth.exchange";
    public static final String AUTH_ROUTING_KEY_SYNC = "auth.sync";

    @Bean
    public TopicExchange AuthSyncExchange() {
        return new TopicExchange(AUTH_EXCHANGE);
    }

    @Bean
    public Queue AuthSyncQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("auth.sync.queue." + currentPort, true);
    }

    @Bean
    public Binding AuthSyncBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(AUTH_ROUTING_KEY_SYNC);
    }
}
