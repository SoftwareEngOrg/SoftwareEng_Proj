package Service;


public class AdminService {

    private boolean loggedIn = false;
    private String username,passwowrd;

    public void login(String username, String password){
        this.username = username;
        this.passwowrd = password;
        loggedIn = true;
    }
    public void logout()
    {
        loggedIn = false;
    }
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
