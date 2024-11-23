package libraryapi.bookservicequery.services;

import libraryapi.bookservicequery.model.Author;
import libraryapi.bookservicequery.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EditBookRequestTest {
    private EditBookRequest editBookRequest;
    private Genre genre;
    private Author author;

    @BeforeEach
    void setUp() {
        genre = new Genre(); // Assume Genre has a no-args constructor and proper setters
        genre.setName("Fiction"); // Example genre name

        author = new Author(); // Assume Author has a no-args constructor and proper setters
        author.setName("John Doe"); // Example author name

        editBookRequest = new EditBookRequest();
    }

    @Test
    void getTitle() {
        editBookRequest.setTitle("The Great Gatsby");
        assertEquals("The Great Gatsby", editBookRequest.getTitle());
    }

    @Test
    void getGenre() {
        editBookRequest.setGenre(genre);
        assertEquals(genre, editBookRequest.getGenre());
    }

    @Test
    void getDescription() {
        editBookRequest.setDescription("A classic novel of the 20th century.");
        assertEquals("A classic novel of the 20th century.", editBookRequest.getDescription());
    }

    @Test
    void getAuthors() {
        List<Author> authors = Arrays.asList(author);
        editBookRequest.setAuthors(authors);
        assertEquals(authors, editBookRequest.getAuthors());
    }

    @Test
    void setTitle() {
        String title = "1984";
        editBookRequest.setTitle(title);
        assertEquals(title, editBookRequest.getTitle());
    }

    @Test
    void setGenre() {
        editBookRequest.setGenre(genre);
        assertEquals(genre, editBookRequest.getGenre());
    }

    @Test
    void setDescription() {
        String description = "Dystopian novel.";
        editBookRequest.setDescription(description);
        assertEquals(description, editBookRequest.getDescription());
    }

    @Test
    void setAuthors() {
        List<Author> authors = Arrays.asList(author);
        editBookRequest.setAuthors(authors);
        assertEquals(authors, editBookRequest.getAuthors());
    }

    @Test
    void testEquals() {
        EditBookRequest anotherRequest = new EditBookRequest();
        anotherRequest.setTitle("Title");
        anotherRequest.setGenre(genre);
        anotherRequest.setDescription("Description");
        anotherRequest.setAuthors(Arrays.asList(author));

        editBookRequest.setTitle("Title");
        editBookRequest.setGenre(genre);
        editBookRequest.setDescription("Description");
        editBookRequest.setAuthors(Arrays.asList(author));

        assertEquals(anotherRequest, editBookRequest);
    }

    @Test
    void canEqual() {
        assertTrue(editBookRequest.canEqual(new EditBookRequest()));
    }
}
