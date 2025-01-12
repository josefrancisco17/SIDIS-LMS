package libraryapi.lendingservicecommand.rabbitMQ.consumer;

import jakarta.persistence.EntityNotFoundException;
import libraryapi.lendingservicecommand.model.*;
import libraryapi.lendingservicecommand.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.lendingservicecommand.rabbitMQ.RabbitMQConfig;
import libraryapi.lendingservicecommand.rabbitMQ.producer.Sender;
import libraryapi.lendingservicecommand.repositories.*;
import libraryapi.lendingservicecommand.services.LendingServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;

import java.util.Objects;
import java.util.Optional;

@Component
public class Receiver {
    @Autowired
    private Environment env;

    @Autowired
    private LendingServiceImpl lendingService;
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private LendingRepository lendingRepository;

    @Autowired
    private BookCoverRepository bookCoverRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReaderPhotoRepository readerPhotoRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private TempLendingRepository tempLendingRepository;

    @Autowired
    private Sender sender;

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
        lendingRepository.save(newLending);
    }

    @RabbitListener(queues = "#{BookSyncQueue.name}")
    public void receiveSyncBook(Message message) {
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ] Received book: " + messageBody);
        Book newBook = RabbitMapper.StringToBook(messageBody);
        if (newBook.getGenre() != null) {
            genreRepository.save(newBook.getGenre());
        }
        if (newBook.getCover() != null) {
            bookCoverRepository.save(newBook.getCover());
        }
        bookRepository.save(newBook);
    }

    @RabbitListener(queues = "#{ReaderSyncQueue.name}")
    public void receiveSyncReader(Message message) {
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ] Received reader: " + messageBody);
        Reader newReader = RabbitMapper.StringToReader(messageBody);
        if (newReader.getReaderPhoto() != null) {
            readerPhotoRepository.save(newReader.getReaderPhoto());
        }
        readerRepository.save(newReader);
    }

    @RabbitListener(queues = "#{RecommendationSyncQueue.name}")
    public void receiveSyncRecommendation(Message message) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        String senderInstancePort = (String) message.getMessageProperties().getHeaders().get("instancePort");

        if (!currentPort.equals(senderInstancePort)) {
            System.out.println("[RabbitMQ] Ignored message: " + senderInstancePort);
            return;
        }
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ]  Recommendation sync: " + messageBody);
        Recommendation recommendation = RabbitMapper.StringToRecommendation(messageBody);
        Optional<TempLending> optionalTempLending = tempLendingRepository.findByLendingCode(recommendation.getLendingCode());

        if (optionalTempLending.isPresent()) {
            TempLending tempLending = optionalTempLending.get();
            Optional<Lending> newLending = lendingRepository.findByLendingCode(tempLending.getLendingCode());
            newLending.ifPresent(lending -> lendingRepository.save(lending));
            tempLendingRepository.delete(tempLending);
            newLending.ifPresent(lending -> sender.sendSyncLending(lending));
        } else {
            throw new EntityNotFoundException("TempLending not found for the given lending code.");
        }
    }
}
