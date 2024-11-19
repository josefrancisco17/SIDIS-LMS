package libraryapi.bookservice.rabbitMQ.consumer;

import libraryapi.bookservice.model.Author;
import libraryapi.bookservice.model.Book;
import libraryapi.bookservice.model.Genre;
import libraryapi.bookservice.rabbitMQ.Mapper.RabbitMapper;
import libraryapi.bookservice.repositories.AuthorRepository;
import libraryapi.bookservice.repositories.BookRepository;
import libraryapi.bookservice.repositories.GenreRepository;
import libraryapi.bookservice.services.AuthorServiceImpl;
import libraryapi.bookservice.services.BookServiceImpl;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class Receiver {
    @Autowired
    private Environment env;

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private AuthorServiceImpl authorService;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private GenreRepository genreRepository;

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

    @RabbitListener(queues = "#{BookQueryQueue.name}")
    @Transactional
    public String handleBooksRequest() {
        List<Book> books = bookRepository.findAll();
        String booksAsString = books.stream()
                .map(Book::toString)
                .collect(Collectors.joining(", "));
        System.out.println("[RabbitMQ] Sending books: " + booksAsString);
        return booksAsString;
    }


    @RabbitListener(queues = "#{AuthorQueryQueue.name}")
    public String handleAuthorsRequest() {
        List<Author> authors = authorRepository.findAll();
        String authorsAsString = authors.stream()
                .map(Author::toString)
                .collect(Collectors.joining(", "));
        System.out.println("[RabbitMQ] Sending authors: " + authorsAsString);
        return authorsAsString;
    }

    @RabbitListener(queues = "#{GenreQueryQueue.name}")
    public String handleGenresRequest() {
        List<Genre> genres = genreRepository.findAll();
        String genresAsString = genres.stream()
                .map(Genre::toString)
                .collect(Collectors.joining(", "));
        System.out.println("[RabbitMQ] Sending genres: " + genresAsString);
        return genresAsString;
    }
}
