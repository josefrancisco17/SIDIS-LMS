package libraryapi.lendingservicequery.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateLendingRequestTest {
    private CreateLendingRequest createLendingRequest;

    @BeforeEach
    void setUp() {
        createLendingRequest = new CreateLendingRequest();
    }

    @Test
    void getReaderId() {
        createLendingRequest.setReaderId(1L);
        assertEquals(1L, createLendingRequest.getReaderId());
    }

    @Test
    void getBookId() {
        createLendingRequest.setBookId(2L);
        assertEquals(2L, createLendingRequest.getBookId());
    }

    @Test
    void setReaderId() {
        createLendingRequest.setReaderId(3L);
        assertEquals(3L, createLendingRequest.getReaderId());
    }

    @Test
    void setBookId() {
        createLendingRequest.setBookId(4L);
        assertEquals(4L, createLendingRequest.getBookId());
    }

    @Test
    void testEquals() {
        CreateLendingRequest anotherRequest = new CreateLendingRequest(1L, 2L);
        createLendingRequest.setReaderId(1L);
        createLendingRequest.setBookId(2L);
        assertEquals(anotherRequest, createLendingRequest);
    }

    @Test
    void canEqual() {
        assertTrue(createLendingRequest.canEqual(new CreateLendingRequest()));
    }

    @Test
    void testToString() {
        createLendingRequest.setReaderId(1L);
        createLendingRequest.setBookId(2L);

        String expectedString = "CreateLendingRequest(readerId=1, bookId=2)";
        assertEquals(expectedString, createLendingRequest.toString());
    }
}
