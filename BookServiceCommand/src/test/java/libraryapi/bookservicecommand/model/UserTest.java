package libraryapi.bookservicecommand.model;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void isAccountNonExpired() {
        User user = new User("user@example.com", "password");
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked() {
        User user = new User("user@example.com", "password");
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired() {
        User user = new User("user@example.com", "password");
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void isEnabled() {
        User user = new User("user@example.com", "password");
        assertTrue(user.isEnabled());
    }

    @Test
    void newUser() {
        User user = User.newUser("user@example.com", "password", "Full Name");
        assertEquals("user@example.com", user.getUsername());
        assertEquals("Full Name", user.getFullName());
    }

    @Test
    void testNewUser() {
        User user = User.newUser("user@example.com", "password", "Full Name", "ROLE_USER");
        assertEquals("user@example.com", user.getUsername());
        assertEquals("Full Name", user.getFullName());
        assertTrue(user.getAuthorities().contains(new Role("ROLE_USER")));
    }

    @Test
    void setPassword() {
        User user = new User("user@example.com", "password");
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    void addAuthority() {
        User user = new User("user@example.com", "password");
        Role role = new Role("ROLE_ADMIN");
        user.addAuthority(role);
        assertTrue(user.getAuthorities().contains(role));
    }

    @Test
    void testIsAccountNonExpired() {
        User user = new User("user@example.com", "password");
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        User user = new User("user@example.com", "password");
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        User user = new User("user@example.com", "password");
        assertTrue(user.isCredentialsNonExpired());
    }


    @Test
    void testIsEnabled() {
        User user = new User("user@example.com", "password");
        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    void getUsername() {
        User user = new User("user@example.com", "password");
        assertEquals("user@example.com", user.getUsername());
    }

    @Test
    void getPassword() {
        User user = new User("user@example.com", "password");
        assertEquals("password", user.getPassword());
    }

    @Test
    void getFullName() {
        User user = new User("user@example.com", "password");
        user.setFullName("Full Name");
        assertEquals("Full Name", user.getFullName());
    }


    @Test
    void setEnabled() {
        User user = new User("user@example.com", "password");
        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    void setFullName() {
        User user = new User("user@example.com", "password");
        user.setFullName("New Full Name");
        assertEquals("New Full Name", user.getFullName());
    }
}
