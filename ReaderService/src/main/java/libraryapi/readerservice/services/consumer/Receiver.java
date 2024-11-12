package libraryapi.readerservice.services.consumer;

import libraryapi.readerservice.configuration.RabbitMQConfig;
import libraryapi.readerservice.services.ReaderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Receiver {
    @Autowired
    private Environment env;

    @RabbitListener(queues = "#{queue.name}") // Dynamically resolve queue name
    public void receiveMessage(org.springframework.amqp.core.Message message) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        String senderInstancePort = (String) message.getMessageProperties().getHeaders().get("instancePort");

        // Ignore messages sent by this instance
        if (currentPort.equals(senderInstancePort)) {
            System.out.println("[RabbitMQ] Ignored message from same instance: " + senderInstancePort);
            return;
        }

        // Process the message
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ] Synchronized Reader: " + messageBody);
    }
}
