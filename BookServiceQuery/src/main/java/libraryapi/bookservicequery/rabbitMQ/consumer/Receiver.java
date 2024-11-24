package libraryapi.bookservicequery.rabbitMQ.consumer;

import libraryapi.bookservicequery.model.Author;
import libraryapi.bookservicequery.model.Book;
import libraryapi.bookservicequery.model.Genre;
import libraryapi.bookservicequery.model.Lending;
import libraryapi.bookservicequery.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.bookservicequery.repositories.AuthorRepository;
import libraryapi.bookservicequery.repositories.BookRepository;
import libraryapi.bookservicequery.repositories.GenreRepository;
import libraryapi.bookservicequery.repositories.LendingRepository;
import libraryapi.bookservicequery.services.AuthorServiceImpl;
import libraryapi.bookservicequery.services.BookServiceImpl;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class Receiver {
    @Autowired
    private Environment env;

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private LendingRepository lendingRepository;

    @RabbitListener(queues = "#{BookSyncQueue.name}")
    public void receiveSyncBook(Message message) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        String senderInstancePort = (String) message.getMessageProperties().getHeaders().get("instancePort");

        if (currentPort.equals(senderInstancePort)) {
            System.out.println("[RabbitMQ] Ignored message from same instance: " + senderInstancePort);
            return;
        }
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ]  Book sync: " + messageBody);
        Book newBook = RabbitMapper.StringToBook(messageBody);
        bookService.manageInternalBook(newBook);
    }

    @RabbitListener(queues = "#{AuthorSyncQueue.name}")
    public void receiveSyncAuthor(Message message) {
        String currentPort = Objects.requireNonNull(env.getProperty("server.port"));
        String senderInstancePort = (String) message.getMessageProperties().getHeaders().get("instancePort");

        if (currentPort.equals(senderInstancePort)) {
            System.out.println("[RabbitMQ] Ignored message from same instance: " + senderInstancePort);
            return;
        }
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ]  Author sync: " + messageBody);
        Author newAuthor = RabbitMapper.StringToAuthor(messageBody);
        authorRepository.save(newAuthor);
    }

    @RabbitListener(queues = "#{LendingSyncQueue.name}")
    public void receiveSyncLending(Message message) {
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ] Received lending: " + messageBody);
        Lending newLending = RabbitMapper.StringToLending(messageBody);
        lendingRepository.save(newLending);
    }
}
