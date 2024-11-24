package libraryapi.bookservicecommand.rabbitMQ.consumer;

import libraryapi.bookservicecommand.model.Author;
import libraryapi.bookservicecommand.model.Book;
import libraryapi.bookservicecommand.model.Genre;
import libraryapi.bookservicecommand.model.Lending;
import libraryapi.bookservicecommand.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.bookservicecommand.repositories.AuthorRepository;
import libraryapi.bookservicecommand.repositories.BookRepository;
import libraryapi.bookservicecommand.repositories.GenreRepository;
import libraryapi.bookservicecommand.repositories.LendingRepository;
import libraryapi.bookservicecommand.services.AuthorServiceImpl;
import libraryapi.bookservicecommand.services.BookServiceImpl;
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
    private AuthorServiceImpl authorService;

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
        authorService.manageInternalAuthor(newAuthor);
    }

    @RabbitListener(queues = "#{LendingSyncQueue.name}")
    public void receiveSyncLending(Message message) {
        String messageBody = new String(message.getBody());
        System.out.println("[RabbitMQ] Received lending: " + messageBody);
        Lending newLending = RabbitMapper.StringToLending(messageBody);
        lendingRepository.save(newLending);
    }

}
