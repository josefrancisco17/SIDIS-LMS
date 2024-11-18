package libraryapi.lendingservice.rabbitMQ.consumer;

import libraryapi.lendingservice.model.Lending;
import libraryapi.lendingservice.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.lendingservice.rabbitMQ.RabbitMQConfig;
import libraryapi.lendingservice.services.LendingServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;

import java.util.Objects;

@Component
public class Receiver {
    @Autowired
    private Environment env;

    @Autowired
    private LendingServiceImpl lendingService;

    @RabbitListener(queues = "#{LendingSyncQueue.name}")
    public void receiveSyncLending(Message message) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        String senderInstancePort = (String) message.getMessageProperties().getHeaders().get("instancePort");

        if (currentPort.equals(senderInstancePort)) {
            System.out.println("[RabbitMQ] Ignored message from same instance: " + senderInstancePort);
            return;
        }
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ]  Lending sync: " + messageBody);
        Lending newLending = RabbitMapper.StringToLending(messageBody);
        lendingService.manageInternalLending(newLending);
    }

    @RabbitListener(queues = "#{LendingQueryQueue.name}")
    public String handleLendingsRequest() {
        String lendings = "[{id:1, bookId:101}, {id:2, bookId:102}]";
        System.out.println("[RabbitMQ] Sending lendings: " + lendings);
        return lendings;
    }
}
