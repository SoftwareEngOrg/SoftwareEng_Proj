package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdminService Tests")
class AdminServiceTest {

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService();
    }

    @Test
    @DisplayName("Initially, admin should not be logged in")
    void testInitialLoggedIn() {
        assertFalse(adminService.isLoggedIn());
    }

    @Test
    @DisplayName("Login should set loggedIn to true")
    void testLogin() {
        adminService.login("user", "123");
        assertTrue(adminService.isLoggedIn());
    }

    @Test
    @DisplayName("Login should store correct username")
    void testLoginStoresUsername() {
        adminService.login("user", "123");
        assertEquals("user", adminService.getUsername());
    }

    @Test
    @DisplayName("Login should store correct password")
    void testLoginStoresPassword() {
        adminService.login("user", "123");
        assertEquals("123", adminService.getPasswowrd());
    }

    @Test
    @DisplayName("Logout should set loggedIn to false")
    void testLogout() {
        adminService.login("user", "123");
        adminService.logout();
        assertFalse(adminService.isLoggedIn());
    }
}
