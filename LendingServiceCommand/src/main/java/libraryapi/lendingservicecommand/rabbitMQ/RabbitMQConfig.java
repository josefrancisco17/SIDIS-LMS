package libraryapi.lendingservicecommand.rabbitMQ;

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
    public static final String BOOK_SYNC_EXCHANGE = "book.sync.exchange";
    public static final String READER_SYNC_EXCHANGE = "reader.sync.exchange";
    public static final String TEMP_LENDING_SYNC_EXCHANGE = "temp.lending.sync.exchange";
    public static final String RECOMMENDATION_SYNC_EXCHANGE = "recommendation.sync.exchange";

    public static final String LENDING_ROUTING_KEY_SYNC = "lending.sync";
    public static final String BOOK_ROUTING_KEY_SYNC = "book.sync";
    public static final String READER_ROUTING_KEY_SYNC = "reader.sync";
    public static final String TEMP_LENDING_ROUTING_KEY_SYNC = "temp.lending.sync";
    public static final String RECOMMENDATION_ROUTING_KEY_SYNC = "recommendation.lending.sync";

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


    //Temp Lendings

    @Bean
    public TopicExchange TempLendingSyncExchange() {
        return new TopicExchange(TEMP_LENDING_SYNC_EXCHANGE);
    }

    @Bean
    public Queue TempLendingSyncQueue() {
        return new Queue("temp.lending.sync.queue", true);
    }

    @Bean
    public Binding TempLendingSyncBinding(Queue TempLendingSyncQueue, TopicExchange TempLendingSyncExchange) {
        return BindingBuilder.bind(TempLendingSyncQueue).to(TempLendingSyncExchange).with(TEMP_LENDING_ROUTING_KEY_SYNC);
    }

    //Recommendation

    @Bean
    public TopicExchange RecommendationSyncExchange() {
        return new TopicExchange(RECOMMENDATION_SYNC_EXCHANGE);
    }

    @Bean
    public Queue RecommendationSyncQueue() {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        return new Queue("recommendation.sync.queue." + currentPort, true);
    }

    @Bean
    public Binding RecommendationSyncBinding(Queue RecommendationSyncQueue, TopicExchange RecommendationSyncExchange) {
        return BindingBuilder.bind(RecommendationSyncQueue).to(RecommendationSyncExchange).with(RECOMMENDATION_ROUTING_KEY_SYNC);
    }
}
