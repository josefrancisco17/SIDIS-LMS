package libraryapi.readerservicequery.rabbitMQ.consumer;

import libraryapi.readerservicequery.model.Reader;
import libraryapi.readerservicequery.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.readerservicequery.repositories.ReaderRepository;
import libraryapi.readerservicequery.services.ReaderServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class Receiver {
    @Autowired
    private Environment env;

    @Autowired
    private ReaderServiceImpl readerService;
    @Autowired
    private ReaderRepository readerRepository;

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
        List<Reader> readers = readerRepository.findAll();
        String readersAsString = readers.stream()
                .map(Reader::toString)
                .collect(Collectors.joining(", "));
        System.out.println("[RabbitMQ] Sending readers: " + readersAsString);
        return readersAsString;
    }
}
