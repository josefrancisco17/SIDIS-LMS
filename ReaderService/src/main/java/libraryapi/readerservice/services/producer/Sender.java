package libraryapi.readerservice.services.producer;

import libraryapi.readerservice.configuration.RabbitMQConfig;
import libraryapi.readerservice.model.Reader;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public void sendReaderUpdate(Reader reader) {
        String message = reader.toString();
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message, msg -> {
            msg.getMessageProperties().setHeader("instancePort", currentPort); // Add sender's ID as a header
            return msg;
        });
    }
}
