package libraryapi.lendingservicequery.rabbitMQ.consumer;

import libraryapi.lendingservicequery.model.Lending;
import libraryapi.lendingservicequery.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.lendingservicequery.repositories.LendingRepository;
import libraryapi.lendingservicequery.services.LendingServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
    @Autowired
    private LendingRepository lendingRepository;

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
        String lendings = lendingRepository.findAll().toString();
        System.out.println("[RabbitMQ] Sending lendings: " + lendings);
        return lendings;
    }
}
