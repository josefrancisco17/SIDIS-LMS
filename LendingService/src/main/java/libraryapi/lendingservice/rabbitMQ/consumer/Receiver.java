package libraryapi.lendingservice.rabbitMQ.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import libraryapi.lendingservice.model.Lending;
import libraryapi.lendingservice.services.LendingService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    @Autowired
    private LendingService lendingService;

    @RabbitListener(queues = "#{LendingQueryQueue.name}")
    public void receiveLendingRequest(Message message) {
        String requestMessage = new String(message.getBody());

        if ("GetAllLendings".equals(requestMessage)) {
            Iterable<Lending> lendings = lendingService.getAllLendings();

            String response = lendings.toString();

            rabbitTemplate.convertAndSend(
                    message.getMessageProperties().getReplyTo(),
                    response
            );
        }
    }
}
