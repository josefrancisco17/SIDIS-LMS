package libraryapi.bookservicequery.services;

import jakarta.transaction.Transactional;
import libraryapi.bookservicequery.model.*;
import libraryapi.bookservicequery.model.*;
import libraryapi.bookservicequery.repositories.*;
import libraryapi.bookservicequery.repositories.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import libraryapi.bookservicequery.util.BookUtil;
import libraryapi.bookservicequery.exceptions.NotFoundException;
import libraryapi.bookservicequery.fileStorage.FileStorageService;
import libraryapi.bookservicequery.fileStorage.UploadFileResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static libraryapi.bookservicequery.util.BookUtil.isValidBookCover;
import static libraryapi.bookservicequery.util.BookUtil.*;

@Service
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final LendingRepository lendingRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository, LendingRepository lendingRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.lendingRepository = lendingRepository;
    }

    public Optional<Book> getBook(final String isbn) {
        return bookRepository.findBookByIsbn(isbn);
    }

    public Page<Book> getBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Iterable<Genre> getTopGenres() {
        return bookRepository.findTopGenres();
    }

    public Iterable<Book> getTopBooks() {
        List<Lending> lendings = lendingRepository.findAll();

        return lendings.stream()
                .collect(Collectors.groupingBy(
                        Lending::getBookId,
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .map(bookId -> bookRepository.findById(bookId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Book> getBooksByAuthorId(Long authorId) {
        return bookRepository.findBooksByAuthorId(authorId);
    }

    public Page<Book> getBooksByGenre(final String genre, Pageable pageable) {
        List<Book> filteredBooks = bookRepository.findAll().stream()
                .filter(book -> book.getGenre().getName().toLowerCase().contains(genre.toLowerCase()))
                .collect(Collectors.toList());
        return toPage(filteredBooks, pageable);
    }

    public Page<Book> getBooksByTitle(final String title, Pageable pageable) {
        List<Book> filteredBooks = bookRepository.findAll().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
        return toPage(filteredBooks, pageable);
    }

    public Page<Book> getBooksByAuthor(final String authorName, Pageable pageable) {
        List<Book> filteredBooks = bookRepository.findAll().stream()
                .filter(book -> book.getAuthors().stream()
                        .anyMatch(author -> author.getName().equalsIgnoreCase(authorName)))
                .collect(Collectors.toList());

        return toPage(filteredBooks, pageable);
    }

    public Page<Book> getBooksByTitleAndGenreAndAuthor(final String genre, final String title, final String authorName, Pageable pageable) {
        List<Book> filteredBooks = bookRepository.findAll().stream()
                .filter(book -> book.getGenre().getName().toLowerCase().contains(genre.toLowerCase()))
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(book -> book.getAuthors().stream()
                        .anyMatch(author -> author.getName().equalsIgnoreCase(authorName)))
                .collect(Collectors.toList());

        return toPage(filteredBooks, pageable);
    }

    public BookCover getBookCover(final String bookId) {
        final var existingBook = bookRepository.findById(Long.parseLong(bookId)).orElseThrow(() -> new NotFoundException("[ERROR] Book not found"));

        if (existingBook.getCover() == null) {
            throw new IllegalArgumentException("[ERROR] Book Cover not found with ID: " + existingBook.getId());
        }

        return existingBook.getCover();
    }

    @Transactional
    public Book manageInternalBook(Book book) {
        List<Author> savedAuthors = new ArrayList<>();
        for (Author author : book.getAuthors()) {
            Author savedAuthor = authorRepository.save(author);
            savedAuthors.add(savedAuthor);
        }
        book.setAuthors(savedAuthors);
        return bookRepository.save(book);
    }
}
