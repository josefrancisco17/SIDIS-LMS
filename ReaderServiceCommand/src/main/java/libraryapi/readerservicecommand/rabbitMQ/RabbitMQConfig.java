package libraryapi.readerservicecommand.rabbitMQ;

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
    public static final String READER_ROUTING_KEY_SYNC = "reader.sync";

    public static final String READER_QUERY_EXCHANGE = "reader.query.exchange";
    public static final String READER_ROUTING_KEY_QUERY = "reader.query";

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

    // Data Author query from other instances

    @Bean
    public DirectExchange ReaderQueryExchange() {
        return new DirectExchange(READER_QUERY_EXCHANGE);
    }

    @Bean
    public Queue ReaderQueryQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("author.query.queue." + currentPort, true);
    }

    @Bean
    public Binding ReaderQueryBinding(Queue ReaderQueryQueue, DirectExchange ReaderQueryExchange) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return BindingBuilder.bind(ReaderQueryQueue).to(ReaderQueryExchange).with(READER_ROUTING_KEY_QUERY + currentPort);
    }
}
