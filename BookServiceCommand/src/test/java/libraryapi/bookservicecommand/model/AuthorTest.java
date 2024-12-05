package libraryapi.bookservicecommand.model;

import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorTest {

    @Test
    void getId() {
        Author author = new Author();
        author.setId(1L);
        assertEquals(1L, author.getId());
    }

    @Test
    void getVersion() {
        Author author = new Author();
        author.setVersion(2L);
        assertEquals(2L, author.getVersion());
    }

    @Test
    void setVersion() {
        Author author = new Author();
        author.setVersion(3L);
        assertEquals(3L, author.getVersion());
    }

    @Test
    void setId() {
        Author author = new Author();
        author.setId(10L);
        assertEquals(10L, author.getId());
    }

    @Test
    void setLents() {
        Author author = new Author();
        author.setLents(5);
        assertEquals(5, author.getLents());
    }

    @Test
    void getLents() {
        Author author = new Author();
        author.setLents(7);
        assertEquals(7, author.getLents());
    }

    @Test
    void getName() {
        Author author = new Author();
        author.setName("John Doe");
        assertEquals("John Doe", author.getName());
    }

    @Test
    void setName() {
        Author author = new Author();
        author.setName("Jane Doe");
        assertEquals("Jane Doe", author.getName());
    }

    @Test
    void getShortBio() {
        Author author = new Author();
        author.setShortBio("This is a short bio.");
        assertEquals("This is a short bio.", author.getShortBio());
    }

    @Test
    void setShortBio() {
        Author author = new Author();
        author.setShortBio("Another short bio.");
        assertEquals("Another short bio.", author.getShortBio());
    }

    @Test
    void getAuthorPhoto() {
        Author author = new Author();
        AuthorPhoto photo = new AuthorPhoto();
        author.setAuthorPhoto(photo);
        assertEquals(photo, author.getAuthorPhoto());
    }

    @Test
    void setAuthorPhoto() {
        Author author = new Author();
        AuthorPhoto photo = new AuthorPhoto();
        author.setAuthorPhoto(photo);
        assertEquals(photo, author.getAuthorPhoto());
    }

    @Test
    void applyPatch() {
        Author author = new Author("Old Name", "Old Bio");
        author.setVersion(1L);
        author.applyPatch(1L, "New Name", "New Bio");

        assertEquals("New Name", author.getName());
        assertEquals("New Bio", author.getShortBio());
    }

    @Test
    void applyPatchThrowsExceptionWhenVersionMismatch() {
        Author author = new Author("Old Name", "Old Bio");
        author.setVersion(1L);

        assertThrows(StaleObjectStateException.class, () -> {
            author.applyPatch(2L, "New Name", "New Bio");
        });
    }

    @Test
    void updateData() {
        Author author = new Author("Old Name", "Old Bio");
        author.setVersion(1L);
        author.updateData(1L, "Updated Name", "Updated Bio");

        assertEquals("Updated Name", author.getName());
        assertEquals("Updated Bio", author.getShortBio());
    }

    @Test
    void updateDataThrowsExceptionWhenVersionMismatch() {
        Author author = new Author("Old Name", "Old Bio");
        author.setVersion(1L);

        assertThrows(StaleObjectStateException.class, () -> {
            author.updateData(2L, "Updated Name", "Updated Bio");
        });
    }

    @Test
    void testToString() {
        Author author = new Author("John Doe", "A brief bio");
        String result = author.toString();
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("A brief bio"));
    }
}
