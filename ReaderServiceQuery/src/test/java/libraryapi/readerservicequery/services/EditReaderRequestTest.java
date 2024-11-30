package libraryapi.readerservicequery.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EditReaderRequestTest {
    private EditReaderRequest editReaderRequest;

    @BeforeEach
    void setUp() {
        editReaderRequest = new EditReaderRequest();
    }

    @Test
    void getName() {
        editReaderRequest.setName("John Doe");
        assertEquals("John Doe", editReaderRequest.getName());
    }

    @Test
    void getEmail() {
        editReaderRequest.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", editReaderRequest.getEmail());
    }

    @Test
    void getDateOfBirth() {
        LocalDate dob = LocalDate.of(1990, 1, 1);
        editReaderRequest.setDateOfBirth(dob);
        assertEquals(dob, editReaderRequest.getDateOfBirth());
    }

    @Test
    void getPhoneNumber() {
        editReaderRequest.setPhoneNumber(123456789);
        assertEquals(123456789, editReaderRequest.getPhoneNumber());
    }

    @Test
    void getGDBRConsent() {
        editReaderRequest.setGDBRConsent(true);
        assertTrue(editReaderRequest.getGDBRConsent());
    }

    @Test
    void getInterests() {
        editReaderRequest.setInterests(Arrays.asList("Reading", "Music"));
        assertEquals(Arrays.asList("Reading", "Music"), editReaderRequest.getInterests());
    }

    @Test
    void setName() {
        editReaderRequest.setName("Jane Doe");
        assertEquals("Jane Doe", editReaderRequest.getName());
    }

    @Test
    void setEmail() {
        editReaderRequest.setEmail("jane.doe@example.com");
        assertEquals("jane.doe@example.com", editReaderRequest.getEmail());
    }

    @Test
    void setDateOfBirth() {
        LocalDate dob = LocalDate.of(1992, 5, 20);
        editReaderRequest.setDateOfBirth(dob);
        assertEquals(dob, editReaderRequest.getDateOfBirth());
    }

    @Test
    void setPhoneNumber() {
        editReaderRequest.setPhoneNumber(987654321);
        assertEquals(987654321, editReaderRequest.getPhoneNumber());
    }

    @Test
    void setGDBRConsent() {
        editReaderRequest.setGDBRConsent(false);
        assertFalse(editReaderRequest.getGDBRConsent());
    }

    @Test
    void setInterests() {
        editReaderRequest.setInterests(Arrays.asList("Traveling", "Cooking"));
        assertEquals(Arrays.asList("Traveling", "Cooking"), editReaderRequest.getInterests());
    }

    @Test
    void testEquals() {
        EditReaderRequest anotherRequest = new EditReaderRequest("John Doe", "john.doe@example.com", LocalDate.of(1990, 1, 1), 123456789, true, Arrays.asList("Reading"));
        editReaderRequest.setName("John Doe");
        editReaderRequest.setEmail("john.doe@example.com");
        editReaderRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        editReaderRequest.setPhoneNumber(123456789);
        editReaderRequest.setGDBRConsent(true);
        editReaderRequest.setInterests(Arrays.asList("Reading"));

        assertEquals(anotherRequest, editReaderRequest);
    }

    @Test
    void canEqual() {
        assertTrue(editReaderRequest.canEqual(new EditReaderRequest()));
    }

    @Test
    void testToString() {
        editReaderRequest.setName("John Doe");
        editReaderRequest.setEmail("john.doe@example.com");
        editReaderRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        editReaderRequest.setPhoneNumber(123456789);
        editReaderRequest.setGDBRConsent(true);
        editReaderRequest.setInterests(Arrays.asList("Reading"));

        String expectedString = "EditReaderRequest(name=John Doe, email=john.doe@example.com, dateOfBirth=1990-01-01, phoneNumber=123456789, GDBRConsent=true, interests=[Reading])";
        assertEquals(expectedString, editReaderRequest.toString());
    }
}
