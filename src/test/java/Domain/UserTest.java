package Domain;

import org.junit.jupiter.api.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Constructor sets all fields correctly (with lastLoginDate)")
    void constructor_setsUsernamePasswordRoleEmailLastLoginDate() {
        // Arrange: Define a sample date
        Date lastLogin = new Date();
        User user = new User("alice", "secret123", "customer", "alice@example.com", lastLogin);

        // Act & Assert: Check if all fields are set correctly
        assertEquals("alice", user.getUsername());
        assertEquals("secret123", user.getPassword());
        assertEquals("customer", user.getRole());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals(lastLogin, user.getLastLoginDate());
    }

    @Test
    @DisplayName("Constructor sets fields correctly (without lastLoginDate)")
    void constructor_setsUsernamePasswordRole() {
        // Arrange
        User user = new User("bob", "pass", "admin");

        // Act & Assert: Check if basic fields are set correctly
        assertEquals("bob", user.getUsername());
        assertEquals("pass", user.getPassword());
        assertEquals("admin", user.getRole());
        assertNull(user.getEmail());
        assertNull(user.getLastLoginDate());
    }

    @Test
    @DisplayName("getUsername returns correct value")
    void getUsername_returnsCorrect() {
        User user = new User("john", "mypassword", "librarian");
        assertEquals("john", user.getUsername());
    }

    @Test
    @DisplayName("getPassword returns correct value")
    void getPassword_returnsCorrect() {
        User user = new User("john", "mypassword", "librarian");
        assertEquals("mypassword", user.getPassword());
    }

    @Test
    @DisplayName("getRole returns correct value")
    void getRole_returnsCorrect() {
        User admin = new User("admin1", "123", "admin");
        assertEquals("admin", admin.getRole());
    }

    @Test
    @DisplayName("toString contains all fields")
    void toString_containsUsernamePasswordRoleEmailAndLastLogin() {
        Date lastLogin = new Date();
        User user = new User("mary", "hello", "customer", "mary@example.com", lastLogin);
        String str = user.toString();

        assertTrue(str.contains("mary"));
        assertTrue(str.contains("hello"));
        assertTrue(str.contains("customer"));
        assertTrue(str.contains("mary@example.com"));
        assertTrue(str.contains("| Last Login: " + new SimpleDateFormat("yyyy-MM-dd").format(lastLogin)));
    }

    @Test
    @DisplayName("getEmail returns correct value")
    void getEmail_returnsCorrect() {
        User user = new User("john", "mypassword", "librarian", "john@example.com", null);
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    @DisplayName("setEmail updates email correctly")
    void setEmail_updatesCorrectly() {
        User user = new User("john", "mypassword", "librarian", "john@example.com", null);
        user.setEmail("john_new@example.com");

        assertEquals("john_new@example.com", user.getEmail());
    }

    @Test
    @DisplayName("getLastLoginDate returns correct value")
    void getLastLoginDate_returnsCorrect() {
        Date lastLogin = new Date();
        User user = new User("john", "mypassword", "librarian", "john@example.com", lastLogin);
        assertEquals(lastLogin, user.getLastLoginDate());
    }

    @Test
    @DisplayName("setLastLoginDate updates lastLoginDate correctly")
    void setLastLoginDate_updatesCorrectly() {
        Date lastLogin = new Date();
        User user = new User("john", "mypassword", "librarian", "john@example.com", null);
        user.setLastLoginDate(lastLogin);

        assertEquals(lastLogin, user.getLastLoginDate());
    }

    @Test
    @DisplayName("getFormattedLastLoginDate returns formatted date")
    void getFormattedLastLoginDate_returnsFormattedDate() {
        Date lastLogin = new Date();
        User user = new User("john", "mypassword", "librarian", "john@example.com", lastLogin);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(lastLogin);

        assertEquals(formattedDate, user.getFormattedLastLoginDate());
    }

    @Test
    @DisplayName("getFormattedLastLoginDate returns 'Never logged in' if date is null")
    void getFormattedLastLoginDate_returnsNeverLoggedInIfNull() {
        User user = new User("john", "mypassword", "librarian", "john@example.com", null);

        assertEquals("Never logged in", user.getFormattedLastLoginDate());
    }
}
