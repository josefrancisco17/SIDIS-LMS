package libraryapi.bookservice.rabbitMQ.producer;

import libraryapi.bookservice.model.Author;
import libraryapi.bookservice.model.Book;
import libraryapi.bookservice.rabbitMQ.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Sender {
    @Autowired
    private Environment env;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public Sender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSyncBook(Book book) {
        String message = book.toString();
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.BOOK_EXCHANGE, RabbitMQConfig.BOOK_ROUTING_KEY_SYNC, message, msg -> {
                msg.getMessageProperties().setHeader("instancePort", currentPort);
                return msg;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSyncAuthor(Author author) {
        String message = author.toString();
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.AUTHOR_EXCHANGE, RabbitMQConfig.AUTHOR_ROUTING_KEY_SYNC, message, msg -> {
                msg.getMessageProperties().setHeader("instancePort", currentPort);
                return msg;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
