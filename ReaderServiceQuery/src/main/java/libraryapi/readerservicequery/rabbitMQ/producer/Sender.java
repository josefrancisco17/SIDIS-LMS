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
}
