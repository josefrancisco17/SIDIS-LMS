package libraryapi.bookservice.rabbitMQ;

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

    public static final String BOOK_EXCHANGE = "book.exchange";
    public static final String BOOK_ROUTING_KEY_SYNC = "book.sync";

    @Bean
    public TopicExchange BookSyncExchange() {
        return new TopicExchange(BOOK_EXCHANGE);
    }

    @Bean
    public Queue BookSyncQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("book.sync.queue." + currentPort, true);
    }

    @Bean
    public Binding BookSyncBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(BOOK_ROUTING_KEY_SYNC);
    }
}
