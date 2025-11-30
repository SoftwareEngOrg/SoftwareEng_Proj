package Domain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {

    private String username;
    private String password;
    private String role;
    private String email;
    private Date lastLoginDate;

    public User(String username , String password , String role , String email,Date lastLoginDate)
    {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.lastLoginDate = lastLoginDate;

    }

    public User(String username , String password , String role)
    {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString()
    {
        return (username + " | " + password + " | " + role + " | " + email+"| Last Login: " + getFormattedLastLoginDate());
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    public String getFormattedLastLoginDate() {
        if (lastLoginDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // Format for just the date
            return sdf.format(lastLoginDate);  // Return formatted date as string
        } else {
            return "Never logged in";
        }
    }
}
