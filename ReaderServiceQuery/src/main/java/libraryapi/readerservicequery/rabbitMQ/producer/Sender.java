package libraryapi.readerservicequery.rabbitMQ.producer;

import io.github.cdimascio.dotenv.Dotenv;
import libraryapi.readerservicequery.model.Book;
import libraryapi.readerservicequery.model.Lending;
import libraryapi.readerservicequery.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.readerservicequery.rabbitMQ.RabbitMQConfig;
import libraryapi.readerservicequery.model.Reader;
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
    private final int ReaderServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_COMMAND_PORT1")));
    private final int ReaderServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_COMMAND_PORT1")));
    private final int LendingServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_COMMAND_PORT1")));
    private final int LendingServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_COMMAND_PORT1")));
    private final int BookServicePort1 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_COMMAND_PORT1")));
    private final int BookServicePort2 = Integer.parseInt(Objects.requireNonNull(dotenv.get("BOOK_COMMAND_PORT1")));


    @Autowired
    public Sender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSyncReader(Reader reader) {
        String message = reader.toString();
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.READER_SYNC_EXCHANGE, RabbitMQConfig.READER_ROUTING_KEY_SYNC, message, msg -> {
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
                lendings = RabbitMapper.StringToLendingList(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lendings;
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
                books = RabbitMapper.stringToBookList(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }
}
