package libraryapi.bookservicecommand.services;

import jakarta.transaction.Transactional;
import libraryapi.bookservicecommand.model.*;
import libraryapi.bookservicecommand.rabbitMQ.producer.Sender;
import libraryapi.bookservicecommand.repositories.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import libraryapi.bookservicecommand.util.BookUtil;
import libraryapi.bookservicecommand.exceptions.NotFoundException;
import libraryapi.bookservicecommand.fileStorage.FileStorageService;
import libraryapi.bookservicecommand.fileStorage.UploadFileResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static libraryapi.bookservicecommand.util.BookUtil.isValidBookCover;
import static libraryapi.bookservicecommand.util.BookUtil.*;

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
