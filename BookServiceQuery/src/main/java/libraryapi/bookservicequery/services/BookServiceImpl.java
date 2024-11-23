package libraryapi.bookservicequery.services;

import jakarta.transaction.Transactional;
import libraryapi.bookservicequery.model.*;
import libraryapi.bookservicequery.model.*;
import libraryapi.bookservicequery.rabbitMQ.producer.Sender;
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
    private final BookCoverRepository bookCoverRepository;
    private final BookRepositoryHTTP bookRepositoryHTTP;
    private final GenreRepository genreRepository;
    private final FileStorageService fileStorageService;
    private final EditBookMapper editBookMapper;
    private final LendingRepositoryHTTP lendingRepositoryHTTP;
    private final AuthorRepository authorRepository;

    @Autowired
    private Sender sender;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BookCoverRepository bookCoverRepository, BookRepositoryHTTP bookRepositoryHTTP, EditBookMapper editBookMapper, GenreRepository genreRepository, FileStorageService fileStorageService, LendingRepositoryHTTP lendingRepositoryHTTP, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.bookCoverRepository = bookCoverRepository;
        this.bookRepositoryHTTP = bookRepositoryHTTP;
        this.editBookMapper = editBookMapper;
        this.genreRepository = genreRepository;
        this.fileStorageService =  fileStorageService;
        this.lendingRepositoryHTTP = lendingRepositoryHTTP;
        this.authorRepository = authorRepository;
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
        //List<Lending> lendings = lendingRepositoryHTTP.getAllLendings();
        List<Lending> lendings = new ArrayList<>();
        try {
            lendings = sender.getLendings();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public Book createBook(final CreateBookRequest resource, MultipartFile coverPhoto) {
        validateCreateBookRequest(resource);

        Book book = editBookMapper.create(resource);
        bookRepository.save(book);

        if (coverPhoto != null) {
            doUploadFile(book.getId().toString(), coverPhoto);
            book.setVersion(book.getVersion() - 1);
        }
        //bookRepositoryHTTP.manageInternalBook(book);
        try {
            sender.sendSyncBook(book);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return book;
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

    @Transactional
    public Book updateBook(final Long id, final EditBookRequest resource, final long desiredVersion) {
        final var book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("[ERROR] Cannot update an object that does not yet exist"));

        final var existingGenre = genreRepository.findById(resource.getGenre().getId()).orElseThrow(() -> new NotFoundException("[ERROR] Genre not found"));

        book.updateData(desiredVersion, resource.getTitle(), resource.getAuthors() ,existingGenre ,resource.getDescription());
        //bookRepositoryHTTP.manageInternalBook(book);
        try {
            sender.sendSyncBook(book);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookRepository.save(book);
    }

    @Transactional
    public Book partialUpdateBook(final Long id, final EditBookRequest resource, final long desiredVersion) {
        final var book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("[ERROR] Cannot update an object that does not yet exist"));

        Genre existingGenre = null;

        if (resource.getGenre() != null) {
            existingGenre = genreRepository.findById(resource.getGenre().getId()).orElseThrow(() -> new NotFoundException("[ERROR] Genre not found"));
        }

        book.applyPatch(desiredVersion, resource.getTitle(), resource.getAuthors(), existingGenre, resource.getDescription());

        //bookRepositoryHTTP.manageInternalBook(book);
        try {
            sender.sendSyncBook(book);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookRepository.save(book);
    }

    private void validateCreateBookRequest(final CreateBookRequest request) {
        String bookTitle = request.getTitle();
        String trimmedTitle = bookTitle.trim();

        if(!bookTitle.equals(trimmedTitle)) {
            throw new IllegalArgumentException("[ERROR] Book Title cannot start or end with spaces.");
        }

        if (!BookUtil.isValidISBN(request.getIsbn())) {
            throw new IllegalArgumentException("[ERROR] ISBN-10 or ISBN-13 invalid ISBN.");
        }

        if (bookRepository.findBookByIsbn(request.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("[ERROR] Book with that ISBN is already registered.");
        }

        if (StringUtils.isBlank(request.getTitle()) ||
                StringUtils.isWhitespace(Character.toString(request.getTitle().charAt(0))) ||
                StringUtils.isWhitespace(Character.toString(request.getTitle().charAt(request.getTitle().length() - 1)))) {
            throw new IllegalArgumentException("[ERROR] Book title is mandatory and cannot start or end with spaces.");
        }

        if (StringUtils.isBlank(request.getGenre().getName()) || StringUtils.isBlank(request.getAuthors().toString())) {
            throw new IllegalArgumentException("[ERROR] Genre and author fields are mandatory.");
        }
    }

    public UploadFileResponse doUploadFile(final String id, final MultipartFile file) {
        if (isValidBookCover(file)) {
            BookCover cover = new BookCover();
            try {
                cover.setImage(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            cover.setContentType(file.getContentType());
            bookCoverRepository.save(cover);
            Book book = bookRepository.getById(Long.parseLong(id));
            book.setCover(cover);
            book.setVersion(book.getVersion() - 1);
            bookRepository.save(book);
        }

        final String fileName = fileStorageService.storeFile(id, file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(fileName)
                .toUriString();

        fileDownloadUri = fileDownloadUri.replace("/covers/", "/cover/");

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }
}
