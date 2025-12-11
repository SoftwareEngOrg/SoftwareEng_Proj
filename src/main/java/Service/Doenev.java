package Service;

import io.github.cdimascio.dotenv.Dotenv;
/**
 * Doenev is a small utility class responsible for loading email credentials
 * from a .env file using the Dotenv library. The loaded EMAIL_USERNAME and
 * EMAIL_PASSWORD values are stored and accessed through getters.
 */
public class Doenev {

    private String username;

    private String password;

    Dotenv dotenv = Dotenv.load();
    String EMAIL = dotenv.get("EMAIL_USERNAME");
    String PASS  = dotenv.get("EMAIL_PASSWORD");

    public Doenev()
    {
        this.username = EMAIL;
        this.password = PASS;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
