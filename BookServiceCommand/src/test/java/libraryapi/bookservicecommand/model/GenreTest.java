package libraryapi.bookservicecommand.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    @Test
    void getId() {
        Genre genre = new Genre();
        genre.setId(1L);
        assertEquals(1L, genre.getId());
    }

    @Test
    void setId() {
        Genre genre = new Genre();
        genre.setId(2L);
        assertEquals(2L, genre.getId());
    }

    @Test
    void getName() {
        Genre genre = new Genre("Fantasy");
        assertEquals("Fantasy", genre.getName());
    }

    @Test
    void setName() {
        Genre genre = new Genre();
        genre.setName("Science Fiction");
        assertEquals("Science Fiction", genre.getName());
    }

    @Test
    void testToString() {
        Genre genre = new Genre(1L, "Thriller");
        String result = genre.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("name='Thriller'"));
    }
}
