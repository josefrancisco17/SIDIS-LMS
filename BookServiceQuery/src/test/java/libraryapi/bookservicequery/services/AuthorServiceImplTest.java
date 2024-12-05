package libraryapi.bookservicequery.services;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
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
        PageRequest pageable = PageRequest.of(0, 10);
        List<Author> authors = Arrays.asList(new Author("Author1", "Bio1", new AuthorPhoto()), new Author("Author2", "Bio2", new AuthorPhoto()));
        Page<Author> authorPage = mock(Page.class);
        when(authorRepository.findAll(pageable)).thenReturn(authorPage);
        when(authorPage.getContent()).thenReturn(authors);

        Page<Author> result = authorService.getAuthors(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(authorRepository).findAll(pageable);
    }

    @Test
    void getAuthorsById() {

        Long authorId = 1L;
        Author author = new Author("Author1", "Bio1", new AuthorPhoto());
        author.setId(authorId);
        when(authorRepository.findAuthorById(authorId)).thenReturn(Optional.of(author));

        Optional<Author> result = authorService.getAuthorsById(authorId);

        assertTrue(result.isPresent());
        assertEquals(authorId, result.get().getId());
        verify(authorRepository).findAuthorById(authorId);
    }

    @Test
    void getAuthorPhoto() {
        Long authorId = 1L;
        Author author = new Author("Author1", "Bio1", new AuthorPhoto());
        AuthorPhoto authorPhoto = new AuthorPhoto();
        author.setId(authorId);
        author.setAuthorPhoto(authorPhoto);
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        AuthorPhoto result = authorService.getAuthorPhoto(String.valueOf(authorId));

        assertNotNull(result);
        assertEquals(authorPhoto, result);
        verify(authorRepository).findById(authorId);
    }

    @Test
    void getAuthorPhoto_ThrowsNotFoundException() {

        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            authorService.getAuthorPhoto(String.valueOf(authorId));
        });
        assertEquals("[ERROR] Author not found", exception.getMessage());
    }

    @Test
    void getAuthorsByName() {
        String name = "Author";
        Author author1 = new Author("Author1", "Bio1", new AuthorPhoto());
        Author author2 = new Author("Author2", "Bio2", new AuthorPhoto());
        List<Author> authors = Arrays.asList(author1, author2);
        when(authorRepository.findAll()).thenReturn(authors);

        List<Author> result = authorService.getAuthorsByName(name);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(author -> author.getName().contains(name)));
        verify(authorRepository).findAll();
    }

    @Test
    void getAuthorBooks() {

        Long authorId = 1L;
        Genre genre = new Genre("Fiction");
        Book book1 = new Book("Book1", "Description1", genre, "ISBN1", Arrays.asList(new Author("Author1", "Bio1", new AuthorPhoto())), new BookCover());
        Book book2 = new Book("Book2", "Description2", genre, "ISBN2", Arrays.asList(new Author("Author1", "Bio1", new AuthorPhoto())), new BookCover());
        List<Book> books = Arrays.asList(book1, book2);
        when(bookRepository.findByAuthors_Id(authorId)).thenReturn(books);

        List<Book> result = authorService.getAuthorBooks(authorId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookRepository).findByAuthors_Id(authorId);
    }

}
