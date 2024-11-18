package libraryapi.bookservice.rabbitMQ.producer;

import io.github.cdimascio.dotenv.Dotenv;
import libraryapi.bookservice.model.Author;
import libraryapi.bookservice.model.Book;
import libraryapi.bookservice.model.Lending;
import libraryapi.bookservice.rabbitMQ.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class Sender {
    @Autowired
    private Environment env;

    private final RabbitTemplate rabbitTemplate;

    private final Dotenv dotenv = Dotenv.load();
    private final int BookServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_PORT1")));
    private final int BookServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_PORT2")));
    private final int LendingServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT1")));
    private final int LendingServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("LENDING_PORT2")));

    @Autowired
    public Sender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSyncBook(Book book) {
        String message = book.toString();
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.BOOK_SYNC_EXCHANGE, RabbitMQConfig.BOOK_ROUTING_KEY_SYNC, message, msg -> {
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
            rabbitTemplate.convertAndSend(RabbitMQConfig.AUTHOR_SYNC_EXCHANGE, RabbitMQConfig.AUTHOR_ROUTING_KEY_SYNC, message, msg -> {
                msg.getMessageProperties().setHeader("instancePort", currentPort);
                return msg;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Lending> getLendings() {
        List<Lending> lendings = new ArrayList<>();

        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        int targetPort = (currentPort == BookServicePort1) ? LendingServicePort1 : LendingServicePort2;

        try {
            String response = (String) rabbitTemplate.convertSendAndReceive(
                    "lending.query.exchange",
                    "lending.query" + targetPort,
                    ""
            );

            if (response != null) {
                System.out.println("[RabbitMQ] Lendings: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lendings;
    }
}
