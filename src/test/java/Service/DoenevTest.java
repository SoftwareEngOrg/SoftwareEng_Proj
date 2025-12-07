package Service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoenevTest {

    @Test
    @DisplayName("Constructor should load EMAIL and PASS from Dotenv and assign them to fields")
    void testConstructorLoadsValues()
    {
        Doenev env = new Doenev();
        assertEquals(env.EMAIL, env.getUsername());
        assertEquals(env.PASS, env.getPassword());
    }

    @Test
    @DisplayName("Setters should correctly update username and password")
    void testSettersAndGetters()
    {
        Doenev env = new Doenev();
        env.setUsername("newUser");
        env.setPassword("newPass");
        assertEquals("newUser", env.getUsername());
        assertEquals("newPass", env.getPassword());
    }
}
