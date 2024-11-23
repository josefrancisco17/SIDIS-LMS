package libraryapi.authservicequery.rabbitMQ.producer;

import libraryapi.authservicequery.model.User;
import libraryapi.authservicequery.rabbitMQ.RabbitMQConfig;
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

    public void sendSyncUser(User user) {
        String message = user.toString();
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.AUTH_SYNC_EXCHANGE, RabbitMQConfig.AUTH_ROUTING_KEY_SYNC, message, msg -> {
                msg.getMessageProperties().setHeader("instancePort", currentPort);
                return msg;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
