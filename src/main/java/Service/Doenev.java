package Service;

import io.github.cdimascio.dotenv.Dotenv;
/**
 * The Doenev class is responsible for loading email credentials (username and password)
 * from environment variables using the Dotenv library.
 * <p>
 * It loads the values of `EMAIL_USERNAME` and `EMAIL_PASSWORD` from a .env file
 * and provides getters and setters to access and modify these credentials.
 * </p>
 */
public class Doenev {

    private String username;

    private String password;
    // Load environment variables from .env file
    Dotenv dotenv = Dotenv.load();
    String EMAIL = dotenv.get("EMAIL_USERNAME");
    String PASS  = dotenv.get("EMAIL_PASSWORD");
    /**
     * Constructs a Doenev object that loads the email username and password
     * from environment variables using the Dotenv library.
     */
    public Doenev()
    {
        this.username = EMAIL;
        this.password = PASS;
    }

    /**
     * Gets the email username.
     *
     * @return the email username loaded from the .env file
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the email username.
     *
     * @param username the email username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * Gets the email password.
     *
     * @return the email password loaded from the .env file
     */
    public String getPassword() {
        return password;
    }
    /**
     * Sets the email password.
     *
     * @param password the email password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
