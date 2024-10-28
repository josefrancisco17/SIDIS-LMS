package libraryapi.authservice.services;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EditUserRequestTest {

    @Test
    void getFullName() {
        EditUserRequest request = new EditUserRequest("John Doe", new HashSet<>());
        assertEquals("John Doe", request.getFullName());
    }

    @Test
    void getAuthorities() {
        Set<String> authorities = new HashSet<>();
        authorities.add("ADMIN");
        EditUserRequest request = new EditUserRequest("John Doe", authorities);
        assertEquals(authorities, request.getAuthorities());
    }

    @Test
    void setFullName() {
        EditUserRequest request = new EditUserRequest();
        request.setFullName("Jane Doe");
        assertEquals("Jane Doe", request.getFullName());
    }

    @Test
    void setAuthorities() {
        EditUserRequest request = new EditUserRequest();
        Set<String> authorities = new HashSet<>();
        authorities.add("USER");
        request.setAuthorities(authorities);
        assertEquals(authorities, request.getAuthorities());
    }

    @Test
    void testEquals() {
        Set<String> authorities1 = new HashSet<>();
        authorities1.add("USER");
        EditUserRequest request1 = new EditUserRequest("John Doe", authorities1);

        Set<String> authorities2 = new HashSet<>();
        authorities2.add("USER");
        EditUserRequest request2 = new EditUserRequest("John Doe", authorities2);

        assertEquals(request1, request2);
    }

    @Test
    void canEqual() {
        EditUserRequest request1 = new EditUserRequest("John Doe", new HashSet<>());
        EditUserRequest request2 = new EditUserRequest("John Doe", new HashSet<>());

        assertTrue(request1.canEqual(request2));
    }

    @Test
    void testHashCode() {
        Set<String> authorities = new HashSet<>();
        authorities.add("USER");
        EditUserRequest request1 = new EditUserRequest("John Doe", authorities);
        EditUserRequest request2 = new EditUserRequest("John Doe", authorities);

        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        Set<String> authorities = new HashSet<>();
        authorities.add("USER");
        EditUserRequest request = new EditUserRequest("John Doe", authorities);

        String expectedString = "EditUserRequest(fullName=John Doe, authorities=[USER])";
        assertEquals(expectedString, request.toString());
    }
}
