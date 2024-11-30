package libraryapi.readerservicecommand.rabbitMQ.producer;

import io.github.cdimascio.dotenv.Dotenv;
import libraryapi.readerservicecommand.model.Book;
import libraryapi.readerservicecommand.model.Lending;
import libraryapi.readerservicecommand.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.readerservicecommand.rabbitMQ.RabbitMQConfig;
import libraryapi.readerservicecommand.model.Reader;
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
}
