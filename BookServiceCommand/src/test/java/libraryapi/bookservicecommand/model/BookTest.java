package libraryapi.bookservicecommand.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void getId() {
        Book book = new Book();
        book.setId(1L);
        assertEquals(1L, book.getId());
    }

    @Test
    void setId() {
        Book book = new Book();
        book.setId(2L);
        assertEquals(2L, book.getId());
    }

    @Test
    void getVersion() {
        Book book = new Book();
        book.setVersion(1L);
        assertEquals(1L, book.getVersion());
    }

    @Test
    void setVersion() {
        Book book = new Book();
        book.setVersion(2L);
        assertEquals(2L, book.getVersion());
    }


    @Test
    void setIsbn() {
        Book book = new Book();
        book.setIsbn("9783161484100");
        assertEquals("9783161484100", book.getIsbn());
    }

    @Test
    void getTitle() {
        Book book = new Book();
        book.setTitle("Sample Title");
        assertEquals("Sample Title", book.getTitle());
    }

    @Test
    void setTitle() {
        Book book = new Book();
        book.setTitle("New Title");
        assertEquals("New Title", book.getTitle());
    }

    @Test
    void getGenre() {
        Genre genre = new Genre();
        Book book = new Book();
        book.setGenre(genre);
        assertEquals(genre, book.getGenre());
    }

    @Test
    void setGenre() {
        Genre genre = new Genre();
        Book book = new Book();
        book.setGenre(genre);
        assertEquals(genre, book.getGenre());
    }


    @Test
    void getCover() {
        BookCover cover = new BookCover();
        Book book = new Book();
        book.setCover(cover);
        assertEquals(cover, book.getCover());
    }

    @Test
    void setCover() {
        BookCover cover = new BookCover();
        Book book = new Book();
        book.setCover(cover);
        assertEquals(cover, book.getCover());
    }

    @Test
    void updateData() {
        Book book = new Book();
        book.setVersion(1L);
        List<Author> authors = new ArrayList<>();
        Genre genre = new Genre();

        book.updateData(1L, "Updated Title", authors, genre, "Updated Description");

        assertEquals("Updated Title", book.getTitle());
        assertEquals(authors, book.getAuthors());
        assertEquals(genre, book.getGenre());
        assertEquals("Updated Description", book.getDescription());
    }

    @Test
    void applyPatch() {
        Book book = new Book();
        book.setVersion(1L);
        List<Author> authors = new ArrayList<>();
        Genre genre = new Genre();

        book.applyPatch(1L, "Patched Title", authors, genre, "Patched Description");

        assertEquals("Patched Title", book.getTitle());
        assertEquals(authors, book.getAuthors());
        assertEquals(genre, book.getGenre());
        assertEquals("Patched Description", book.getDescription());
    }

    @Test
    void getAuthors() {
        List<Author> authors = new ArrayList<>();
        Book book = new Book();
        book.setAuthors(authors);
        assertEquals(authors, book.getAuthors());
    }

    @Test
    void setAuthors() {
        List<Author> authors = new ArrayList<>();
        Book book = new Book();
        book.setAuthors(authors);
        assertEquals(authors, book.getAuthors());
    }
}
