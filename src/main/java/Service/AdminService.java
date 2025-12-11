package Service;

/**
 * Service class for handling admin login and logout functionality.
 */
public class AdminService {

    private boolean loggedIn = false;
    private String username, passwowrd;

    /**
     * Logs in the admin with the given username and password.
     *
     * @param username the admin username
     * @param password the admin password
     */
    public void login(String username, String password) {
        this.username = username;
        this.passwowrd = password;
        loggedIn = true;
    }

    /**
     * Logs out the admin.
     */
    public void logout() {
        loggedIn = false;
    }

    /**
     * Checks if the admin is currently logged in.
     *
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Gets the admin username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the admin password.
     *
     * @return the password
     */
    public String getPasswowrd() {
        return passwowrd;
    }
}
