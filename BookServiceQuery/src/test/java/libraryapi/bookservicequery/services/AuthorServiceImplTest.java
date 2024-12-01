package libraryapi.bookservicequery.services;

import libraryapi.bookservicequery.model.Author;
import libraryapi.bookservicequery.model.AuthorPhoto;
import libraryapi.bookservicequery.model.Book;
import libraryapi.bookservicequery.model.BookCover;
import libraryapi.bookservicequery.model.Genre;
import libraryapi.bookservicequery.model.Lending;
import libraryapi.bookservicequery.repositories.AuthorRepository;
import libraryapi.bookservicequery.repositories.BookRepository;
import libraryapi.bookservicequery.repositories.LendingRepository;
import libraryapi.bookservicequery.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LendingRepository lendingRepository;

    private AuthorServiceImpl authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorService = new AuthorServiceImpl(authorRepository, bookService, bookRepository);
    }

    @Test
    void getAuthors() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        List<Author> authors = Arrays.asList(new Author("Author1", "Bio1", new AuthorPhoto()), new Author("Author2", "Bio2", new AuthorPhoto()));
        Page<Author> authorPage = mock(Page.class);
        when(authorRepository.findAll(pageable)).thenReturn(authorPage);
        when(authorPage.getContent()).thenReturn(authors);

        // Act
        Page<Author> result = authorService.getAuthors(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(authorRepository).findAll(pageable);
    }

    @Test
    void getAuthorsById() {
        // Arrange
        Long authorId = 1L;
        Author author = new Author("Author1", "Bio1", new AuthorPhoto());
        author.setId(authorId);
        when(authorRepository.findAuthorById(authorId)).thenReturn(Optional.of(author));

        // Act
        Optional<Author> result = authorService.getAuthorsById(authorId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(authorId, result.get().getId());
        verify(authorRepository).findAuthorById(authorId);
    }

    @Test
    void getAuthorPhoto() {
        // Arrange
        Long authorId = 1L;
        Author author = new Author("Author1", "Bio1", new AuthorPhoto());
        AuthorPhoto authorPhoto = new AuthorPhoto();
        author.setId(authorId);
        author.setAuthorPhoto(authorPhoto);
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        // Act
        AuthorPhoto result = authorService.getAuthorPhoto(String.valueOf(authorId));

        // Assert
        assertNotNull(result);
        assertEquals(authorPhoto, result);
        verify(authorRepository).findById(authorId);
    }

    @Test
    void getAuthorPhoto_ThrowsNotFoundException() {
        // Arrange
        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            authorService.getAuthorPhoto(String.valueOf(authorId));
        });
        assertEquals("[ERROR] Author not found", exception.getMessage());
    }

    @Test
    void getAuthorsByName() {
        // Arrange
        String name = "Author";
        Author author1 = new Author("Author1", "Bio1", new AuthorPhoto());
        Author author2 = new Author("Author2", "Bio2", new AuthorPhoto());
        List<Author> authors = Arrays.asList(author1, author2);
        when(authorRepository.findAll()).thenReturn(authors);

        // Act
        List<Author> result = authorService.getAuthorsByName(name);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(author -> author.getName().contains(name)));
        verify(authorRepository).findAll();
    }

    @Test
    void getAuthorBooks() {
        // Arrange
        Long authorId = 1L;
        Genre genre = new Genre("Fiction");
        Book book1 = new Book("Book1", "Description1", genre, "ISBN1", Arrays.asList(new Author("Author1", "Bio1", new AuthorPhoto())), new BookCover());
        Book book2 = new Book("Book2", "Description2", genre, "ISBN2", Arrays.asList(new Author("Author1", "Bio1", new AuthorPhoto())), new BookCover());
        List<Book> books = Arrays.asList(book1, book2);
        when(bookRepository.findByAuthors_Id(authorId)).thenReturn(books);

        // Act
        List<Book> result = authorService.getAuthorBooks(authorId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookRepository).findByAuthors_Id(authorId);
    }

    @Test
    void getTop5Authors() {
        // Arrange
        List<Lending> lendings = Arrays.asList(
                new Lending("Lending1", 1L, 1L, "BORROWED", LocalDate.now(), LocalDate.now().plusDays(7), LocalDate.now().plusDays(14), false, 0.0f, "No notes"),
                new Lending("Lending2", 2L, 1L, "BORROWED", LocalDate.now(), LocalDate.now().plusDays(7), LocalDate.now().plusDays(14), false, 0.0f, "No notes")
        );
        when(lendingRepository.findAll()).thenReturn(lendings);

        Author author1 = new Author("Author1", "Bio1", new AuthorPhoto());
        author1.setId(1L);
        Author author2 = new Author("Author2", "Bio2", new AuthorPhoto());
        author2.setId(2L);
        List<Author> authors = Arrays.asList(author1, author2);
        when(authorRepository.findAll()).thenReturn(authors);
        when(bookService.getBooksByAuthorId(1L)).thenReturn(Arrays.asList(new Book("Book1", "Description1", new Genre("Fiction"), "ISBN1", Arrays.asList(author1), new BookCover())));
        when(bookService.getBooksByAuthorId(2L)).thenReturn(Collections.singletonList(new Book("Book2", "Description2", new Genre("Fiction"), "ISBN2", Arrays.asList(author2), new BookCover())));

        // Act
        List<Author> result = authorService.getTop5Authors();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(author1, result.get(0));
        assertEquals(author2, result.get(1));
        verify(lendingRepository).findAll();
        verify(authorRepository).findAll();
    }
}
