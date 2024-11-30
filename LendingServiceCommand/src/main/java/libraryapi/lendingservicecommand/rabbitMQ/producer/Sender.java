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
}
