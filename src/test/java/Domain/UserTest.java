// src/test/java/Domain/UserTest.java
package Domain;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Constructor sets all fields correctly")
    void constructor_setsUsernamePasswordRole() {
        User user = new User("alice", "secret123", "customer");

        assertEquals("alice", user.getUsername());
        assertEquals("secret123", user.getPassword());
        assertEquals("customer", user.getRole());
    }

    @Test
    @DisplayName("getUsername returns correct value")
    void getUsername_returnsCorrect() {
        User user = new User("bob", "pass", "admin");
        assertEquals("bob", user.getUsername());
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
    void toString_containsUsernamePasswordRole() {
        User user = new User("mary", "hello", "customer");
        String str = user.toString();

        assertTrue(str.contains("mary"));
        assertTrue(str.contains("hello"));
        assertTrue(str.contains("customer"));
        assertTrue(str.contains("|"));
    }
}