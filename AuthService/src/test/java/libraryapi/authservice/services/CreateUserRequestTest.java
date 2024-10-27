package libraryapi.authservice.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.HashSet;
import java.util.Set;

class CreateUserRequestTest {

    private CreateUserRequest createUserRequest;
    private Validator validator;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest("test@example.com", "Test User", "password123", "password123");

        // Setup para validação de anotações
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void getUsername() {
        assertEquals("test@example.com", createUserRequest.getUsername());
    }

    @Test
    void getFullName() {
        assertEquals("Test User", createUserRequest.getFullName());
    }

    @Test
    void getPassword() {
        assertEquals("password123", createUserRequest.getPassword());
    }

    @Test
    void getRePassword() {
        assertEquals("password123", createUserRequest.getRePassword());
    }

    @Test
    void getAuthorities() {
        Set<String> authorities = new HashSet<>();
        createUserRequest.setAuthorities(authorities);
        assertEquals(authorities, createUserRequest.getAuthorities());
    }

    @Test
    void setUsername() {
        createUserRequest.setUsername("newemail@example.com");
        assertEquals("newemail@example.com", createUserRequest.getUsername());
    }

    @Test
    void setFullName() {
        createUserRequest.setFullName("New User");
        assertEquals("New User", createUserRequest.getFullName());
    }

    @Test
    void setPassword() {
        createUserRequest.setPassword("newPassword");
        assertEquals("newPassword", createUserRequest.getPassword());
    }

    @Test
    void setRePassword() {
        createUserRequest.setRePassword("newPassword");
        assertEquals("newPassword", createUserRequest.getRePassword());
    }

    @Test
    void setAuthorities() {
        Set<String> authorities = new HashSet<>();
        authorities.add("ROLE_USER");
        createUserRequest.setAuthorities(authorities);
        assertEquals(authorities, createUserRequest.getAuthorities());
    }

    @Test
    void testEquals() {
        CreateUserRequest anotherRequest = new CreateUserRequest("test@example.com", "Test User", "password123", "password123");
        assertEquals(createUserRequest, anotherRequest);
    }

    @Test
    void canEqual() {
        CreateUserRequest anotherRequest = new CreateUserRequest("test@example.com", "Test User", "password123", "password123");
        assertTrue(createUserRequest.canEqual(anotherRequest));
    }

    @Test
    void testHashCode() {
        CreateUserRequest anotherRequest = new CreateUserRequest("test@example.com", "Test User", "password123", "password123");
        assertEquals(createUserRequest.hashCode(), anotherRequest.hashCode());
    }

    @Test
    void testToString() {
        String expectedString = "CreateUserRequest(username=test@example.com, fullName=Test User, password=password123, rePassword=password123, authorities=[])";
        assertEquals(expectedString, createUserRequest.toString());
    }

    @Test
    void testConstructorWithDefaults() {
        CreateUserRequest newUserRequest = new CreateUserRequest("test2@example.com", "User Test", "pass1234");
        assertEquals("test2@example.com", newUserRequest.getUsername());
        assertEquals("User Test", newUserRequest.getFullName());
        assertEquals("pass1234", newUserRequest.getPassword());
        assertEquals("pass1234", newUserRequest.getRePassword());
    }
}

