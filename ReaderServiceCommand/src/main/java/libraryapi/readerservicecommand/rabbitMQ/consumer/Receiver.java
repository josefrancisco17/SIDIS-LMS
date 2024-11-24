package libraryapi.readerservicecommand.rabbitMQ.consumer;

import libraryapi.readerservicecommand.model.Reader;
import libraryapi.readerservicecommand.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.readerservicecommand.repositories.ReaderRepository;
import libraryapi.readerservicecommand.services.ReaderServiceImpl;
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
        readerRepository.save(newReader);
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
