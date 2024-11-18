package libraryapi.readerservice.rabbitMQ.consumer;

import libraryapi.readerservice.model.Reader;
import libraryapi.readerservice.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.readerservice.services.ReaderServiceImpl;
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
    private ReaderServiceImpl readerService;

    @RabbitListener(queues = "#{ReaderSyncQueue.name}")
    public void receiveSyncReader(Message message) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        String senderInstancePort = (String) message.getMessageProperties().getHeaders().get("instancePort");

        if (currentPort.equals(senderInstancePort)) {
            System.out.println("[RabbitMQ] Ignored message from same instance: " + senderInstancePort);
            return;
        }
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ]  Reader sync: " + messageBody);
        Reader newReader = RabbitMapper.StringToReader(messageBody);
        readerService.manageInternalReader(newReader);
    }

    @RabbitListener(queues = "#{ReaderQueryQueue.name}")
    public String handleReadersRequest() {
        String readers = "[{id:1, bookId:101}, {id:2, bookId:102}]";
        System.out.println("[RabbitMQ] Sending readers: " + readers);
        return readers;
    }
}
