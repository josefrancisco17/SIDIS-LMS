package libraryapi.bookservicequery.services;

import jakarta.transaction.Transactional;
import libraryapi.bookservicequery.model.*;
import libraryapi.bookservicequery.model.Author;
import libraryapi.bookservicequery.model.AuthorPhoto;
import libraryapi.bookservicequery.model.Book;
import libraryapi.bookservicequery.model.Lending;
import libraryapi.bookservicequery.repositories.*;
import libraryapi.bookservicequery.repositories.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import libraryapi.bookservicequery.exceptions.NotFoundException;
import libraryapi.bookservicequery.fileStorage.FileStorageService;
import libraryapi.bookservicequery.fileStorage.UploadFileResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static libraryapi.bookservicequery.util.AuthorUtil.isValidAuthorPhoto;

@Service
public class AuthorServiceImpl implements AuthorService{
    private final AuthorRepository authorRepository;
    private final EditAuthorMapper editAuthorMapper;
    private final AuthorPhotoRepository authorPhotoRepository;
    private final FileStorageService fileStorageService;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final BookRepositoryHTTP bookRepositoryHTTP;
    private final AuthorRepositoryHTTP authorRepositoryHTTP;
    private final LendingRepositoryHTTP lendingRepositoryHTTP;

    @Autowired
    private LendingRepository lendingRepository;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, EditAuthorMapper editAuthorMapper, AuthorPhotoRepository authorPhotoRepository, FileStorageService fileStorageService, BookService bookService, BookRepository bookRepository, BookRepositoryHTTP bookRepositoryHTTP, AuthorRepositoryHTTP authorRepositoryHTTP, LendingRepositoryHTTP lendingRepositoryHTTP) {
        this.authorRepository = authorRepository;
        this.editAuthorMapper = editAuthorMapper;
        this.authorPhotoRepository = authorPhotoRepository;
        this.fileStorageService = fileStorageService;
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.bookRepositoryHTTP = bookRepositoryHTTP;
        this.authorRepositoryHTTP = authorRepositoryHTTP;
        this.lendingRepositoryHTTP = lendingRepositoryHTTP;
    }

    public Page<Author> getAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }

    public Optional<Author> getAuthorsById(final Long id) {
        return authorRepository.findAuthorById(id);
    }

    public AuthorPhoto getAuthorPhoto(final String authorId) {
        final var existingAuthor = authorRepository.findById(Long.parseLong(authorId)).orElseThrow(() -> new NotFoundException("[ERROR] Author not found"));

        if (existingAuthor.getAuthorPhoto() == null) {
            throw new IllegalArgumentException("[ERROR] Author Photo not found with ID: " + existingAuthor.getId());
        }

        return existingAuthor.getAuthorPhoto();
    }

    public List<Author> getAuthorsByName(final String name) {
        return authorRepository.findAll()
                .stream()
                .filter(author -> author
                        .getName()
                        .toLowerCase()
                        .contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> getAuthorBooks(Long authorId) {
        return bookRepository.findByAuthors_Id(authorId);
    }

    public List<Author> getTop5Authors() {
        List<Lending> lendings = lendingRepository.findAll();

        List<Author> authors = authorRepository.findAll();
        for (Author author : authors) {
            List<Book> booksByAuthor = bookService.getBooksByAuthorId(author.getId());
            int totalLents = 0;
            for (Book book : booksByAuthor) {
                for (Lending lending : lendings) {
                    if (lending.getBookId().equals(book.getId())) {
                        totalLents++;
                    }
                }
            }
            author.setLents(totalLents);
        }
        authors.sort((a1, a2) -> Integer.compare(a2.getLents(), a1.getLents()));
        return authors.subList(0, Math.min(5, authors.size()));
    }

    public List<Book> getCoAuthorsBooks(Long authorId) {
        List<Book> allBooks = bookRepository.findBooksByAuthorId(authorId);
        Set<Book> coAuthorBooks = new HashSet<>();
        for (Book book : allBooks) {
            if (book.getAuthors().size() > 1) {
                coAuthorBooks.add(book);
            }
        }

        return new ArrayList<>(coAuthorBooks);
    }
}