package Service;


/**
 * Service for handling admin login and logout.
 */
public class AdminService {

    private boolean loggedIn = false;
    private String username,passwowrd;
    /**
     * Logs the admin in with the provided credentials.
     *
     * @param username the admin's username
     * @param password the admin's password
     */
    public void login(String username, String password){
        this.username = username;
        this.passwowrd = password;
        loggedIn = true;
    }
    /**
     * Logs the admin out.
     */
    public void logout()
    {
        loggedIn = false;
    }
    /**
     * Checks if the admin is logged in.
     *
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn()
    {
        return loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswowrd() {
        return passwowrd;
    }
}
