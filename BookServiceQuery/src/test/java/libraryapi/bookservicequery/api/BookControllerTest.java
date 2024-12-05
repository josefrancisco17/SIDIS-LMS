package libraryapi.bookservicequery.api;

import libraryapi.bookservicequery.model.*;
import libraryapi.bookservicequery.services.BookServiceImpl;
import libraryapi.bookservicequery.services.GenreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.*;


import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BookControllerTest {

    @Mock
    private BookServiceImpl bookService;

    @Mock
    private BookViewMapper bookViewMapper;

    @Mock
    private GenreViewMapper genreViewMapper;

    @Mock
    private LentBookViewMapper lentBookViewMapper;

    @Mock
    private GenreServiceImpl genreService;

    private BookController bookController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookController = new BookController(bookService, bookViewMapper, genreViewMapper, lentBookViewMapper, genreService);
    }

    @Test
    void getAllBooks() {
        List<Book> books = Arrays.asList(new Book(), new Book());

        when(bookService.getAllBooks()).thenReturn(books);

        Iterable<Book> bookList = bookController.getAllBooks();

        assertNotNull(bookList);
        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void getAllGenres() {
        List<Genre> genres = Arrays.asList(new Genre(), new Genre());

        when(genreService.getAllGenres()).thenReturn(genres);

        Iterable<Genre> genreList = bookController.getAllGenres();

        assertNotNull(genreList);
        verify(genreService, times(1)).getAllGenres();
    }

    @Test
    void getTopGenres() {
        List<Genre> topGenres = Arrays.asList(new Genre(), new Genre());

        when(bookService.getTopGenres()).thenReturn(topGenres);
        when(bookService.getAllBooks()).thenReturn(new ArrayList<>());
        when(genreViewMapper.toGenreView(anyList(), anyList())).thenReturn(Collections.singletonList(new GenreView()));

        Iterable<GenreView> genreViews = bookController.getTopGenres();

        assertNotNull(genreViews);
        verify(bookService, times(1)).getTopGenres();
    }

    @Test
    void getTopBooks() {
        List<Book> topBooks = Arrays.asList(new Book(), new Book());

        when(bookService.getTopBooks()).thenReturn(topBooks);
        when(lentBookViewMapper.toLentBookView(anyList())).thenReturn(Collections.singletonList(new LentBookView()));

        Iterable<LentBookView> lentBookViews = bookController.getTopBooks();

        assertNotNull(lentBookViews);
        verify(bookService, times(1)).getTopBooks();
    }

}
