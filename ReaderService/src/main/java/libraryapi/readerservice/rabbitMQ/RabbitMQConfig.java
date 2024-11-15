package libraryapi.readerservice.rabbitMQ;

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

    public static final String READER_EXCHANGE = "reader.exchange";
    public static final String READER_ROUTING_KEY_SYNC = "reader.sync";

    @Bean
    public TopicExchange ReaderSyncExchange() {
        return new TopicExchange(READER_EXCHANGE);
    }

    @Bean
    public Queue ReaderSyncQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("reader.sync.queue." + currentPort, true);
    }

    @Bean
    public Binding ReaderSyncBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(READER_ROUTING_KEY_SYNC);
    }
}
