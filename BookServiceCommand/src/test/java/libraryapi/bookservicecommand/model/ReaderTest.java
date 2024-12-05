package libraryapi.bookservicecommand.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ReaderTest {

    @Test
    void getId() {
        Reader reader = new Reader();
        reader.setId(1L);
        assertEquals(1L, reader.getId());
    }

    @Test
    void setId() {
        Reader reader = new Reader();
        reader.setId(2L);
        assertEquals(2L, reader.getId());
    }

    @Test
    void getReaderCode() {
        Reader reader = new Reader();
        reader.setReaderCode("R001");
        assertEquals("R001", reader.getReaderCode());
    }

    @Test
    void setReaderCode() {
        Reader reader = new Reader();
        reader.setReaderCode("R002");
        assertEquals("R002", reader.getReaderCode());
    }

    @Test
    void getName() {
        Reader reader = new Reader();
        reader.setName("John Doe");
        assertEquals("John Doe", reader.getName());
    }

    @Test
    void setName() {
        Reader reader = new Reader();
        reader.setName("Jane Doe");
        assertEquals("Jane Doe", reader.getName());
    }

    @Test
    void getVersion() {
        Reader reader = new Reader();
        reader.setVersion(1);
        assertEquals(1, reader.getVersion());
    }

    @Test
    void setVersion() {
        Reader reader = new Reader();
        reader.setVersion(2);
        assertEquals(2, reader.getVersion());
    }

    @Test
    void getEmail() {
        Reader reader = new Reader();
        reader.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", reader.getEmail());
    }

    @Test
    void setEmail() {
        Reader reader = new Reader();
        reader.setEmail("jane.doe@example.com");
        assertEquals("jane.doe@example.com", reader.getEmail());
    }

    @Test
    void getDateOfBirth() {
        Reader reader = new Reader();
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        reader.setDateOfBirth(birthDate);
        assertEquals(birthDate, reader.getDateOfBirth());
    }

    @Test
    void setDateOfBirth() {
        Reader reader = new Reader();
        LocalDate birthDate = LocalDate.of(1992, 6, 20);
        reader.setDateOfBirth(birthDate);
        assertEquals(birthDate, reader.getDateOfBirth());
    }

    @Test
    void getAge() {
        Reader reader = new Reader();
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        reader.setDateOfBirth(birthDate);
        assertEquals(34, reader.getAge());  // Assuming current date is 2024
    }

    @Test
    void setAge() {
        Reader reader = new Reader();
        reader.setAge(35);
        assertEquals(35, reader.getAge());
    }

    @Test
    void getLents() {
        Reader reader = new Reader();
        reader.setLents(5);
        assertEquals(5, reader.getLents());
    }

    @Test
    void setLents() {
        Reader reader = new Reader();
        reader.setLents(10);
        assertEquals(10, reader.getLents());
    }

    @Test
    void getPhoneNumber() {
        Reader reader = new Reader();
        reader.setPhoneNumber(123456789);
        assertEquals(123456789, reader.getPhoneNumber());
    }

    @Test
    void setPhoneNumber() {
        Reader reader = new Reader();
        reader.setPhoneNumber(987654321);
        assertEquals(987654321, reader.getPhoneNumber());
    }

    @Test
    void getGDBRConsent() {
        Reader reader = new Reader();
        reader.setGDBRConsent(true);
        assertTrue(reader.getGDBRConsent());
    }

    @Test
    void setGDBRConsent() {
        Reader reader = new Reader();
        reader.setGDBRConsent(false);
        assertFalse(reader.getGDBRConsent());
    }

    @Test
    void getInterests() {
        Reader reader = new Reader();
        reader.setInterests(Arrays.asList("Reading", "Technology"));
        assertEquals(Arrays.asList("Reading", "Technology"), reader.getInterests());
    }

    @Test
    void setInterests() {
        Reader reader = new Reader();
        reader.setInterests(Arrays.asList("Music", "Travel"));
        assertEquals(Arrays.asList("Music", "Travel"), reader.getInterests());
    }

    @Test
    void getReaderPhoto() {
        Reader reader = new Reader();
        ReaderPhoto photo = new ReaderPhoto();
        reader.setReaderPhoto(photo);
        assertEquals(photo, reader.getReaderPhoto());
    }

    @Test
    void setReaderPhoto() {
        Reader reader = new Reader();
        ReaderPhoto photo = new ReaderPhoto();
        reader.setReaderPhoto(photo);
        assertEquals(photo, reader.getReaderPhoto());
    }

    @Test
    void updateData() {
        Reader reader = new Reader();
        reader.setVersion(1);
        reader.setName("John Doe");
        reader.setEmail("john.doe@example.com");
        reader.setDateOfBirth(LocalDate.of(1990, 5, 15));
        reader.setPhoneNumber(123456789);
        reader.setGDBRConsent(true);
        reader.setInterests(Arrays.asList("Reading", "Music"));

        // Simulating the update
        reader.updateData(1, "John Updated", "john.updated@example.com", LocalDate.of(1991, 6, 25), 987654321, false, Arrays.asList("Writing", "Travel"));

        assertEquals("John Updated", reader.getName());
        assertEquals("john.updated@example.com", reader.getEmail());
        assertEquals(LocalDate.of(1991, 6, 25), reader.getDateOfBirth());
        assertEquals(987654321, reader.getPhoneNumber());
        assertFalse(reader.getGDBRConsent());
        assertEquals(Arrays.asList("Writing", "Travel"), reader.getInterests());
    }

    @Test
    void applyPatch() {
        Reader reader = new Reader();
        reader.setVersion(1);
        reader.setName("John Doe");
        reader.setEmail("john.doe@example.com");
        reader.setDateOfBirth(LocalDate.of(1990, 5, 15));
        reader.setPhoneNumber(123456789);
        reader.setGDBRConsent(true);
        reader.setInterests(Arrays.asList("Reading", "Technology"));

        // Apply patch with partial data
        reader.applyPatch(1, "John Patches", null, null, null, null, Arrays.asList("Music"));

        assertEquals("John Patches", reader.getName());
        assertEquals("john.doe@example.com", reader.getEmail());  // Email stays unchanged
        assertEquals(Arrays.asList("Music"), reader.getInterests());  // Interests updated
    }

    @Test
    void testToString() {
        Reader reader = new Reader("R001", "John Doe", "john.doe@example.com", LocalDate.of(1990, 5, 15), 123456789, true, Arrays.asList("Reading", "Technology"));
        String expectedString = "Reader{id=null, readerCode='R001', version=0, name='John Doe', email='john.doe@example.com', dateOfBirth=1990-05-15, age=34, phoneNumber=123456789, GDBRConsent=true, interests=[Reading, Technology], ReaderPhoto=null}";
        assertEquals(expectedString, reader.toString());
    }
}
