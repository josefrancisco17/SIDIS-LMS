package libraryapi.readerservicequery.rabbitMQ.consumer;

import libraryapi.readerservicequery.model.Book;
import libraryapi.readerservicequery.model.Lending;
import libraryapi.readerservicequery.model.Reader;
import libraryapi.readerservicequery.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.readerservicequery.repositories.*;
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

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LendingRepository lendingRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private BookCoverRepository bookCoverRepository;

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

    @RabbitListener(queues = "#{LendingSyncQueue.name}")
    public void receiveSyncLending(Message message) {
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ] Received lending: " + messageBody);
        Lending newLending = RabbitMapper.StringToLending(messageBody);
        lendingRepository.save(newLending);
    }
}
