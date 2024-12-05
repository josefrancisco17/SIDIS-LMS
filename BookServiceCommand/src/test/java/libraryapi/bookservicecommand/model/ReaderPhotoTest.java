package libraryapi.bookservicecommand.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReaderPhotoTest {

    @Test
    void getId() {
        ReaderPhoto readerPhoto = new ReaderPhoto();
        readerPhoto.setId(1L);
        assertEquals(1L, readerPhoto.getId());
    }

    @Test
    void setId() {
        ReaderPhoto readerPhoto = new ReaderPhoto();
        readerPhoto.setId(2L);
        assertEquals(2L, readerPhoto.getId());
    }

    @Test
    void getReader() {
        Reader reader = new Reader();
        ReaderPhoto readerPhoto = new ReaderPhoto();
        readerPhoto.setReader(reader);
        assertEquals(reader, readerPhoto.getReader());
    }

    @Test
    void setReader() {
        Reader reader = new Reader();
        ReaderPhoto readerPhoto = new ReaderPhoto();
        readerPhoto.setReader(reader);
        assertEquals(reader, readerPhoto.getReader());
    }

    @Test
    void getImage() {
        byte[] image = {1, 2, 3, 4, 5};
        ReaderPhoto readerPhoto = new ReaderPhoto();
        readerPhoto.setImage(image);
        assertArrayEquals(image, readerPhoto.getImage());
    }

    @Test
    void setImage() {
        byte[] image = {1, 2, 3, 4, 5};
        ReaderPhoto readerPhoto = new ReaderPhoto();
        readerPhoto.setImage(image);
        assertArrayEquals(image, readerPhoto.getImage());
    }

    @Test
    void getContentType() {
        ReaderPhoto readerPhoto = new ReaderPhoto();
        readerPhoto.setContentType("image/jpeg");
        assertEquals("image/jpeg", readerPhoto.getContentType());
    }

    @Test
    void setContentType() {
        ReaderPhoto readerPhoto = new ReaderPhoto();
        readerPhoto.setContentType("image/png");
        assertEquals("image/png", readerPhoto.getContentType());
    }

}
