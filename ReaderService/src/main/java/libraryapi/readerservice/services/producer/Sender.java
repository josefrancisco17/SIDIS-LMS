package libraryapi.readerservice.services.producer;

import libraryapi.readerservice.configuration.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Sender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "routing.key", message);
        System.out.println("Sent Message: " + message);
    }
}