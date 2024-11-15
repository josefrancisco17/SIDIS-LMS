package libraryapi.readerservice.rabbitMQ.producer;

import libraryapi.readerservice.model.Lending;
import libraryapi.readerservice.rabbitMQ.RabbitMQConfig;
import libraryapi.readerservice.model.Reader;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    public void SyncReader(Reader reader) {
        String message = reader.toString();
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        rabbitTemplate.convertAndSend(RabbitMQConfig.READER_EXCHANGE_NAME, RabbitMQConfig.READER_ROUTING_KEY_SYNC, message, msg -> {
            msg.getMessageProperties().setHeader("instancePort", currentPort);
            return msg;
        });
    }
}
