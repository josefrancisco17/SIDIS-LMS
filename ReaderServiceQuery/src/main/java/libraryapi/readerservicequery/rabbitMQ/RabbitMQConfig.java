package libraryapi.readerservicequery.rabbitMQ;

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

    public static final String READER_SYNC_EXCHANGE = "reader.sync.exchange";
    public static final String BOOK_SYNC_EXCHANGE = "book.sync.exchange";
    public static final String LENDING_SYNC_EXCHANGE = "lending.sync.exchange";
    public static final String READER_ROUTING_KEY_SYNC = "reader.sync";
    public static final String BOOK_ROUTING_KEY_SYNC = "book.sync";
    public static final String LENDING_ROUTING_KEY_SYNC = "lending.sync";

    @Bean
    public TopicExchange ReaderSyncExchange() {
        return new TopicExchange(READER_SYNC_EXCHANGE);
    }

    @Bean
    public Queue ReaderSyncQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("reader.sync.queue." + currentPort, true);
    }

    @Bean
    public Binding ReaderSyncBinding(Queue ReaderSyncQueue, TopicExchange ReaderSyncExchange) {
        return BindingBuilder.bind(ReaderSyncQueue).to(ReaderSyncExchange).with(READER_ROUTING_KEY_SYNC);
    }

    //Sync Books

    @Bean
    public TopicExchange BookSyncExchange() {
        return new TopicExchange(BOOK_SYNC_EXCHANGE);
    }

    @Bean
    public Queue BookSyncQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("book.sync.queue." + currentPort, true);
    }

    @Bean
    public Binding BookSyncBinding(Queue BookSyncQueue, TopicExchange BookSyncExchange) {
        return BindingBuilder.bind(BookSyncQueue).to(BookSyncExchange).with(BOOK_ROUTING_KEY_SYNC);
    }

    //Sync Lendings

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
}
