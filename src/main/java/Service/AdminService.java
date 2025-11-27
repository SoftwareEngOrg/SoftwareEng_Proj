package Service;

import Domain.User;

public class AdminService {

    private boolean loggedIn = false;
    private FileUserRepository fileUser = new FileUserRepository();

    public boolean login(String username, String password){
        User u = fileUser.findUser(username, password);

        if(u != null && u.getRole().equals("admin"))
        {
            loggedIn = true;
            return true;
        }
        return false;
    }


    public void logout()
    {
        loggedIn = false;
    }

    public boolean isLoggedIn()
    {
        return loggedIn;
    }
}
