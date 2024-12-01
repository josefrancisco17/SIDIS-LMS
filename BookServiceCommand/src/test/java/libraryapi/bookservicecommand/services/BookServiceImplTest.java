package libraryapi.bookservicecommand.services;

import libraryapi.bookservicecommand.exceptions.NotFoundException;
import libraryapi.bookservicecommand.model.*;
import libraryapi.bookservicecommand.repositories.*;
import libraryapi.bookservicecommand.fileStorage.FileStorageService;
import libraryapi.bookservicecommand.fileStorage.UploadFileResponse;
import libraryapi.bookservicecommand.rabbitMQ.producer.Sender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCoverRepository bookCoverRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private EditBookMapper editBookMapper;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private Sender sender;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBook() {
        // Arrange
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("The Great Book");
        request.setIsbn("978-3-16-148410-0");
        request.setGenre(new Genre("Fiction"));

        // Use valid Author constructor
        Author author = new Author("Author", "Name");
        request.setAuthors(List.of(author));

        Book book = new Book();
        book.setId(1L);
        book.setTitle("The Great Book");

        when(editBookMapper.create(request)).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        MultipartFile coverPhoto = mock(MultipartFile.class);
        when(coverPhoto.getOriginalFilename()).thenReturn("cover.jpg");

        // Act
        Book createdBook = bookService.createBook(request, coverPhoto);

        // Assert
        assertNotNull(createdBook);
        assertEquals("The Great Book", createdBook.getTitle());
        verify(bookRepository).save(any(Book.class));
        verify(sender).sendSyncBook(any(Book.class));
    }

    @Test
    void manageInternalBook() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        Author author = new Author("Author", "Name");
        book.setAuthors(List.of(author));

        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        // Act
        Book result = bookService.manageInternalBook(book);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getAuthors().size());
        verify(bookRepository).save(any(Book.class));
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void updateBook() {
        // Arrange
        Long bookId = 1L;
        EditBookRequest request = new EditBookRequest();
        request.setTitle("Updated Book Title");
        request.setGenre(new Genre("Drama"));

        // Use valid Author constructor
        Author author = new Author("Updated", "Author");
        request.setAuthors(List.of(author));
        request.setDescription("Updated Description");

        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Old Title");

        Genre genre = new Genre("Drama");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(genreRepository.findById(anyLong())).thenReturn(Optional.of(genre));
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

        // Act
        Book updatedBook = bookService.updateBook(bookId, request, 1L);

        // Assert
        assertNotNull(updatedBook);
        assertEquals("Updated Book Title", updatedBook.getTitle());
        verify(bookRepository).save(any(Book.class));
        verify(sender).sendSyncBook(any(Book.class));
    }

    @Test
    void updateBook_NotFound_ThrowsException() {
        // Arrange
        Long bookId = 1L;
        EditBookRequest request = new EditBookRequest();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            bookService.updateBook(bookId, request, 1L);
        });
        verify(bookRepository, never()).save(any());
    }

    @Test
    void doUploadFile() throws IOException {
        // Arrange
        String bookId = "1";
        MultipartFile file = mock(MultipartFile.class);
        byte[] fileData = new byte[]{1, 2, 3};
        when(file.getBytes()).thenReturn(fileData);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("cover.jpg");

        BookCover bookCover = new BookCover();
        bookCover.setContentType("image/jpeg");
        bookCover.setImage(fileData);

        Book book = new Book();
        book.setId(1L);

        when(bookRepository.getById(1L)).thenReturn(book);
        when(fileStorageService.storeFile(bookId, file)).thenReturn("cover.jpg");

        // Act
        UploadFileResponse response = bookService.doUploadFile(bookId, file);

        // Assert
        assertNotNull(response);
        assertTrue(response.getFileName().contains("cover.jpg"));
        verify(bookCoverRepository).save(any(BookCover.class));
        verify(bookRepository).save(book);
    }

    @Test
    void validateCreateBookRequest_InvalidRequest_ThrowsException() {
        // Arrange
        CreateBookRequest invalidRequest = new CreateBookRequest();
        invalidRequest.setTitle("");
        invalidRequest.setIsbn("123");
        invalidRequest.setGenre(new Genre(""));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.createBook(invalidRequest, null);
        });

        assertEquals("[ERROR] Book Title cannot start or end with spaces.", exception.getMessage());
    }
}
