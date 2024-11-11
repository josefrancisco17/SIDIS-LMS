package libraryapi.readerservice.services.consumer;

import libraryapi.readerservice.configuration.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        System.out.println("Received Message: " + message);
        // Handle the message here
    }
}
