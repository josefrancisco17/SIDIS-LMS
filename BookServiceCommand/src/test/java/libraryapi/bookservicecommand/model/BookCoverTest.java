package libraryapi.bookservicecommand.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookCoverTest {

    @Test
    void getId() {
        BookCover bookCover = new BookCover();
        bookCover.setId(1L);
        assertEquals(1L, bookCover.getId());
    }

    @Test
    void setId() {
        BookCover bookCover = new BookCover();
        bookCover.setId(2L);
        assertEquals(2L, bookCover.getId());
    }

    @Test
    void getBook() {
        Book book = new Book();
        BookCover bookCover = new BookCover();
        bookCover.setBook(book);
        assertEquals(book, bookCover.getBook());
    }

    @Test
    void setBook() {
        Book book = new Book();
        BookCover bookCover = new BookCover();
        bookCover.setBook(book);
        assertEquals(book, bookCover.getBook());
    }

    @Test
    void getImage() {
        byte[] image = new byte[]{1, 2, 3};
        BookCover bookCover = new BookCover();
        bookCover.setImage(image);
        assertArrayEquals(image, bookCover.getImage());
    }

    @Test
    void setImage() {
        byte[] image = new byte[]{1, 2, 3};
        BookCover bookCover = new BookCover();
        bookCover.setImage(image);
        assertArrayEquals(image, bookCover.getImage());
    }

    @Test
    void getContentType() {
        BookCover bookCover = new BookCover();
        bookCover.setContentType("image/png");
        assertEquals("image/png", bookCover.getContentType());
    }

    @Test
    void setContentType() {
        BookCover bookCover = new BookCover();
        bookCover.setContentType("image/jpeg");
        assertEquals("image/jpeg", bookCover.getContentType());
    }

}
