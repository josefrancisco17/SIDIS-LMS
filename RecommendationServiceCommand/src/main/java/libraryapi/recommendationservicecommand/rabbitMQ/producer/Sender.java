package libraryapi.recommendationservicecommand.rabbitMQ.producer;

import io.github.cdimascio.dotenv.Dotenv;
import libraryapi.recommendationservicecommand.model.*;
import libraryapi.recommendationservicecommand.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.recommendationservicecommand.rabbitMQ.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class Sender {
    @Autowired
    private Environment env;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public Sender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSyncRecommendation(Recommendation recommendation, String port) {
        String message = recommendation.toString();
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.RECOMMENDATION_SYNC_EXCHANGE, RabbitMQConfig.RECOMMENDATION_ROUTING_KEY_SYNC, message, msg -> {
                msg.getMessageProperties().setHeader("instancePort", port);
                return msg;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
