package libraryapi.authservicecommand.rabbitMQ;

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

    public static final String READER_SYNC_EXCHANGE = "reader.sync.exchange";
    public static final String READER_ROUTING_KEY_SYNC = "reader.sync";


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

    //Reader sync

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
}
