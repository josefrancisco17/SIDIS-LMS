package libraryapi.bookservicecommand.rabbitMQ;

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

    public static final String BOOK_SYNC_EXCHANGE = "book.sync.exchange";
    public static final String BOOK_ROUTING_KEY_SYNC = "book.sync";
    public static final String AUTHOR_SYNC_EXCHANGE = "author.sync.exchange";
    public static final String AUTHOR_ROUTING_KEY_SYNC = "author.sync";

    public static final String BOOK_QUERY_EXCHANGE = "book.query.exchange";
    public static final String BOOK_ROUTING_KEY_QUERY = "book.query";

    public static final String AUTHOR_QUERY_EXCHANGE = "author.query.exchange";
    public static final String AUTHOR_ROUTING_KEY_QUERY = "author.query";

    public static final String GENRE_QUERY_EXCHANGE = "genre.query.exchange";
    public static final String GENRE_ROUTING_KEY_QUERY = "genre.query";


    // Book Configuration
    @Bean
    public TopicExchange bookSyncExchange() {
        return new TopicExchange(BOOK_SYNC_EXCHANGE);
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
        return new TopicExchange(AUTHOR_SYNC_EXCHANGE);
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

    // Data Book query from other instances

    @Bean
    public DirectExchange BookQueryExchange() {
        return new DirectExchange(BOOK_QUERY_EXCHANGE);
    }

    @Bean
    public Queue BookQueryQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("book.query.queue." + currentPort, true);
    }

    @Bean
    public Binding BookQueryBinding(Queue BookQueryQueue, DirectExchange BookQueryExchange) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return BindingBuilder.bind(BookQueryQueue).to(BookQueryExchange).with(BOOK_ROUTING_KEY_QUERY + currentPort);
    }

    // Data Author query from other instances

    @Bean
    public DirectExchange AuthorQueryExchange() {
        return new DirectExchange(AUTHOR_QUERY_EXCHANGE);
    }

    @Bean
    public Queue AuthorQueryQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("author.query.queue." + currentPort, true);
    }

    @Bean
    public Binding AuthorQueryBinding(Queue AuthorQueryQueue, DirectExchange AuthorQueryExchange) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return BindingBuilder.bind(AuthorQueryQueue).to(AuthorQueryExchange).with(AUTHOR_ROUTING_KEY_QUERY + currentPort);
    }

    // Data Author query from other instances

    @Bean
    public DirectExchange GenreQueryExchange() {
        return new DirectExchange(GENRE_QUERY_EXCHANGE);
    }

    @Bean
    public Queue GenreQueryQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("genre.query.queue." + currentPort, true);
    }

    @Bean
    public Binding GenreQueryBinding(Queue GenreQueryQueue, DirectExchange GenreQueryExchange) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return BindingBuilder.bind(GenreQueryQueue).to(GenreQueryExchange).with(GENRE_ROUTING_KEY_QUERY + currentPort);
    }
}
