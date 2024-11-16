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
    public static final String AUTHOR_EXCHANGE = "author.exchange";
    public static final String AUTHOR_ROUTING_KEY_SYNC = "author.sync";

    // Book Configuration
    @Bean
    public TopicExchange bookSyncExchange() {
        return new TopicExchange(BOOK_EXCHANGE);
    }

    @Bean
    public Queue BookSyncQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("book.sync.queue." + currentPort, true);
    }

    @Bean
    public Binding bookSyncBinding(Queue BookSyncQueue, TopicExchange bookSyncExchange) {
        return BindingBuilder.bind(BookSyncQueue).to(bookSyncExchange).with(BOOK_ROUTING_KEY_SYNC);
    }

    // Author Configuration
    @Bean
    public TopicExchange authorSyncExchange() {
        return new TopicExchange(AUTHOR_EXCHANGE);
    }

    @Bean
    public Queue AuthorSyncQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("author.sync.queue." + currentPort, true);
    }

    @Bean
    public Binding authorSyncBinding(Queue AuthorSyncQueue, TopicExchange authorSyncExchange) {
        return BindingBuilder.bind(AuthorSyncQueue).to(authorSyncExchange).with(AUTHOR_ROUTING_KEY_SYNC);
    }
}
