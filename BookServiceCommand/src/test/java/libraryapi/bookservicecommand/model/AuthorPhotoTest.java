package libraryapi.bookservicecommand.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorPhotoTest {

    @Test
    void getId() {
        AuthorPhoto authorPhoto = new AuthorPhoto();
        authorPhoto.setId(1L);
        assertEquals(1L, authorPhoto.getId());
    }

    @Test
    void setId() {
        AuthorPhoto authorPhoto = new AuthorPhoto();
        authorPhoto.setId(2L);
        assertEquals(2L, authorPhoto.getId());
    }

    @Test
    void getAuthor() {
        Author author = new Author();
        AuthorPhoto authorPhoto = new AuthorPhoto();
        authorPhoto.setAuthor(author);
        assertEquals(author, authorPhoto.getAuthor());
    }

    @Test
    void setAuthor() {
        Author author = new Author();
        AuthorPhoto authorPhoto = new AuthorPhoto();
        authorPhoto.setAuthor(author);
        assertEquals(author, authorPhoto.getAuthor());
    }

    @Test
    void getImage() {
        byte[] image = {1, 2, 3};
        AuthorPhoto authorPhoto = new AuthorPhoto();
        authorPhoto.setImage(image);
        assertArrayEquals(image, authorPhoto.getImage());
    }

    @Test
    void setImage() {
        byte[] image = {4, 5, 6};
        AuthorPhoto authorPhoto = new AuthorPhoto();
        authorPhoto.setImage(image);
        assertArrayEquals(image, authorPhoto.getImage());
    }

    @Test
    void getContentType() {
        AuthorPhoto authorPhoto = new AuthorPhoto();
        authorPhoto.setContentType("image/png");
        assertEquals("image/png", authorPhoto.getContentType());
    }

    @Test
    void setContentType() {
        AuthorPhoto authorPhoto = new AuthorPhoto();
        authorPhoto.setContentType("image/jpeg");
        assertEquals("image/jpeg", authorPhoto.getContentType());
    }

    @Test
    void testToString() {
        AuthorPhoto authorPhoto = new AuthorPhoto();
        authorPhoto.setId(1L);
        authorPhoto.setContentType("image/png");
        authorPhoto.setImage(new byte[]{1, 2, 3});
        String result = authorPhoto.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("contentType='image/png'"));
        assertTrue(result.contains("image=[1, 2, 3]"));
    }
}
