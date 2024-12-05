package libraryapi.bookservicequery.api;

import libraryapi.bookservicequery.model.Author;
import libraryapi.bookservicequery.model.Book;
import libraryapi.bookservicequery.model.AuthorPhoto;
import libraryapi.bookservicequery.services.AuthorServiceImpl;
import libraryapi.bookservicequery.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthorControllerTest {

    @Mock
    private AuthorServiceImpl authorService;

    @Mock
    private AuthorViewMapper authorViewMapper;

    @Mock
    private BookViewMapper bookViewMapper;

    @Mock
    private AuthorLentsViewMapper authorLentsViewMapper;

    private AuthorController authorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorController = new AuthorController(authorService, authorViewMapper, bookViewMapper, authorLentsViewMapper);
    }


    @Test
    void getAuthor() {
        Long authorId = 1L;
        Author author = new Author();
        author.setId(authorId);

        when(authorService.getAuthorsById(authorId)).thenReturn(Optional.of(author));
        when(authorViewMapper.toAuthorView(any(Author.class))).thenReturn(new AuthorView());

        ResponseEntity<AuthorView> response = authorController.getAuthor(authorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(authorService, times(1)).getAuthorsById(authorId);
    }

    @Test
    void getAuthorsByName() {
        String authorName = "John Doe";
        Author author = new Author();
        author.setName(authorName);

        when(authorService.getAuthorsByName(authorName)).thenReturn(Collections.singletonList(author));
        when(authorViewMapper.toAuthorView(any(Author.class))).thenReturn(new AuthorView());

        Iterable<AuthorView> authorViews = authorController.getAuthors(authorName);

        assertNotNull(authorViews);
        verify(authorService, times(1)).getAuthorsByName(authorName);
    }

    @Test
    void getAuthorCoAuthors() {
        Long authorId = 1L;
        List<Book> books = Arrays.asList(new Book(), new Book());
        Pageable pageable = PageRequest.of(0, 10);

        when(authorService.getCoAuthorsBooks(authorId)).thenReturn(books);
        when(bookViewMapper.toBookView(any(Book.class))).thenReturn(new BookView());

        List<BookView> bookViews = authorController.getAuthorCoAuthors(0, 10, authorId);

        assertEquals(2, bookViews.size());
        verify(authorService, times(1)).getCoAuthorsBooks(authorId);
    }

    @Test
    void getTop5Authors() {
        List<Author> authors = Arrays.asList(new Author(), new Author(), new Author(), new Author(), new Author());

        when(authorService.getTop5Authors()).thenReturn(authors);
        when(authorLentsViewMapper.toAuthorLentsView(authors)).thenReturn(Arrays.asList(new AuthorLentsView()));

        Iterable<AuthorLentsView> topAuthors = authorController.getTop5Authors();

        assertNotNull(topAuthors);
        verify(authorService, times(1)).getTop5Authors();
    }

    @Test
    void getAuthorBooks() {
        Long authorId = 1L;
        List<Book> books = Arrays.asList(new Book(), new Book());
        Pageable pageable = PageRequest.of(0, 10);

        when(authorService.getAuthorBooks(authorId)).thenReturn(books);
        when(bookViewMapper.toBookView(any(Book.class))).thenReturn(new BookView());

        List<BookView> bookViews = authorController.getAuthorBooks(0, 10, authorId);

        assertEquals(2, bookViews.size());
        verify(authorService, times(1)).getAuthorBooks(authorId);
    }

    @Test
    void getBookCover() {
        String authorId = "1";
        AuthorPhoto authorPhoto = new AuthorPhoto();
        authorPhoto.setImage(new byte[]{1, 2, 3});
        authorPhoto.setContentType("image/jpeg");

        when(authorService.getAuthorPhoto(authorId)).thenReturn(authorPhoto);

        ResponseEntity<Resource> response = authorController.getBookCover(authorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(authorService, times(1)).getAuthorPhoto(authorId);
    }
}
