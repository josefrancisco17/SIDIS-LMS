package libraryapi.lendingservicequery.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EditLendingRequestTest {
    private EditLendingRequest editLendingRequest;

    @BeforeEach
    void setUp() {
        editLendingRequest = new EditLendingRequest();
    }

    @Test
    void getId() {
        editLendingRequest.setId(1L);
        assertEquals(1L, editLendingRequest.getId());
    }

    @Test
    void getLendingCode() {
        editLendingRequest.setLendingCode("LEND123");
        assertEquals("LEND123", editLendingRequest.getLendingCode());
    }

    @Test
    void getComment() {
        editLendingRequest.setComment("First lending");
        assertEquals("First lending", editLendingRequest.getComment());
    }

    @Test
    void setId() {
        editLendingRequest.setId(2L);
        assertEquals(2L, editLendingRequest.getId());
    }

    @Test
    void setLendingCode() {
        editLendingRequest.setLendingCode("LEND456");
        assertEquals("LEND456", editLendingRequest.getLendingCode());
    }

    @Test
    void setComment() {
        editLendingRequest.setComment("Second lending");
        assertEquals("Second lending", editLendingRequest.getComment());
    }

    @Test
    void testEquals() {
        EditLendingRequest anotherRequest = new EditLendingRequest(1L, "LEND123", "First lending");
        editLendingRequest.setId(1L);
        editLendingRequest.setLendingCode("LEND123");
        editLendingRequest.setComment("First lending");
        assertEquals(anotherRequest, editLendingRequest);
    }

    @Test
    void canEqual() {
        assertTrue(editLendingRequest.canEqual(new EditLendingRequest()));
    }

    @Test
    void testToString() {
        editLendingRequest.setId(1L);
        editLendingRequest.setLendingCode("LEND123");
        editLendingRequest.setComment("First lending");

        String expectedString = "EditLendingRequest(id=1, lendingCode=LEND123, comment=First lending)";
        assertEquals(expectedString, editLendingRequest.toString());
    }
}
