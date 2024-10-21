package libraryapi.bookservice.services;

import jakarta.transaction.Transactional;
import libraryapi.bookservice.model.*;
import libraryapi.bookservice.repositories.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import libraryapi.bookservice.util.BookUtil;
import libraryapi.bookservice.exceptions.NotFoundException;
import libraryapi.bookservice.fileStorage.FileStorageService;
import libraryapi.bookservice.fileStorage.UploadFileResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static libraryapi.bookservice.util.BookUtil.isValidBookCover;
import static libraryapi.bookservice.util.BookUtil.*;

@Service
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;
    private final BookCoverRepository bookCoverRepository;
    private final BookRepositoryHTTP bookRepositoryHTTP;
    private final GenreRepository genreRepository;
    private final FileStorageService fileStorageService;
    private final EditBookMapper editBookMapper;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BookCoverRepository bookCoverRepository, BookRepositoryHTTP bookRepositoryHTTP, EditBookMapper editBookMapper, GenreRepository genreRepository, FileStorageService fileStorageService) {
        this.bookRepository = bookRepository;
        this.bookCoverRepository = bookCoverRepository;
        this.bookRepositoryHTTP = bookRepositoryHTTP;
        this.editBookMapper = editBookMapper;
        this.genreRepository = genreRepository;
        this.fileStorageService =  fileStorageService;
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
        List<Lending> lendings = bookRepositoryHTTP.getAllLendings();

        Map<Long, Long> lendingCountMap = lendings.stream()
                .collect(Collectors.groupingBy(
                        Lending::getBookId,
                        Collectors.counting()
                ));

        List<Long> topBookIds = lendingCountMap.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        return topBookIds.stream()
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

        System.out.println("Book created: " + book.getVersion());
        System.out.println("Book created: " + book);

        bookRepository.save(book);

        if (coverPhoto != null) {
            doUploadFile(book.getId().toString(), coverPhoto);
        }

        Book newBook = bookRepository.getById(book.getId());
        System.out.println("Book created: " + book.getVersion());

        bookRepositoryHTTP.manageInternalBook(newBook);

        return newBook;
    }


        @Transactional
        public Book manageInternalBook(Book book) {
            System.out.println("Managing internal book: " + book.getVersion());
            return bookRepository.save(book);
        }


        @Transactional
        public Book updateBook(final Long id, final EditBookRequest resource, final long desiredVersion) {
            final var book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("[ERROR] Cannot update an object that does not yet exist"));

            final var existingGenre = genreRepository.findById(resource.getGenre().getId()).orElseThrow(() -> new NotFoundException("[ERROR] Genre not found"));

            book.updateData(desiredVersion, resource.getTitle(), resource.getAuthors() ,existingGenre ,resource.getDescription());
            bookRepositoryHTTP.manageInternalBook(book);
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

            bookRepositoryHTTP.manageInternalBook(book);
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

        @Transactional
        public UploadFileResponse uploadBookCover(final String id, final MultipartFile file) {
            UploadFileResponse up = doUploadFile(id, file);

            Book book = bookRepository.findById(Long.valueOf(id))
                    .orElseThrow(() -> new NotFoundException("Book not found"));

            bookRepositoryHTTP.manageInternalBook(book);

            return up;
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
                bookRepository.save(book);
            }

            final String fileName = fileStorageService.storeFile(id, file);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(fileName)
                    .toUriString();

            fileDownloadUri = fileDownloadUri.replace("/covers/", "/cover/");

            return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
        }
}
