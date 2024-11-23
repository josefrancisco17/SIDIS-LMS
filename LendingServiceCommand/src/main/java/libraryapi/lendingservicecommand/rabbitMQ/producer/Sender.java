package libraryapi.lendingservicecommand.rabbitMQ.producer;

import io.github.cdimascio.dotenv.Dotenv;
import libraryapi.lendingservicecommand.model.Book;
import libraryapi.lendingservicecommand.model.Genre;
import libraryapi.lendingservicecommand.model.Lending;
import libraryapi.lendingservicecommand.model.Reader;
import libraryapi.lendingservicecommand.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.lendingservicecommand.rabbitMQ.RabbitMQConfig;
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
    private final int LendingServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_COMMAND_PORT1")));
    private final int LendingServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_COMMAND_PORT1")));
    private final int ReaderServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_COMMAND_PORT1")));
    private final int ReaderServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_COMMAND_PORT1")));
    private final int BookServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_COMMAND_PORT1")));
    private final int BookServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_COMMAND_PORT1")));

    @Autowired
    public Sender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSyncLending(Lending lending) {
        String message = lending.toString();
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.LENDING_SYNC_EXCHANGE, RabbitMQConfig.LENDING_ROUTING_KEY_SYNC, message, msg -> {
                msg.getMessageProperties().setHeader("instancePort", currentPort);
                return msg;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Reader> getReaders() {
        List<Reader> readers = new ArrayList<>();

        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        int targetPort = (currentPort == LendingServicePort1) ? ReaderServicePort1 : ReaderServicePort2;

        try {
            String response = (String) rabbitTemplate.convertSendAndReceive(
                    "reader.query.exchange",
                    "reader.query" + targetPort,
                    ""
            );

            if (response != null) {
                System.out.println("[RabbitMQ] Readers: " + response);
                readers = RabbitMapper.StringToReaderList(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return readers;
    }

    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();

        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        int targetPort = (currentPort == ReaderServicePort1) ? BookServicePort1 : BookServicePort2;

        try {
            String response = (String) rabbitTemplate.convertSendAndReceive(
                    "book.query.exchange",
                    "book.query" + targetPort,
                    ""
            );

            if (response != null) {
                System.out.println("[RabbitMQ] Books: " + response);
                books = RabbitMapper.StringToBookList(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }

    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();

        int currentPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        int targetPort = (currentPort == LendingServicePort1) ? BookServicePort1 : BookServicePort2;

        try {
            String response = (String) rabbitTemplate.convertSendAndReceive(
                    "genre.query.exchange",
                    "genre.query" + targetPort,
                    ""
            );

            if (response != null) {
                System.out.println("[RabbitMQ] Genres: " + response);
                genres = RabbitMapper.stringToGenreList(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return genres;
    }
}
