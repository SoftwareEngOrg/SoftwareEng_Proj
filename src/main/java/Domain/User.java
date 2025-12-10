package Domain;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a user in the system with login credentials and other personal information.
 */
public class User {

    private String username;
    private String password;
    private String role;
    private String email;
    private Date lastLoginDate;

    /**
     * Creates a new user with the specified details.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @param role the role of the user (e.g., "admin", "member")
     * @param email the email address of the user
     * @param lastLoginDate the date the user last logged in
     */
    public User(String username, String password, String role, String email, Date lastLoginDate) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * Creates a new user with the specified details, without the last login date.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @param role the role of the user (e.g., "admin", "member")
     */
    public User(String username, String password, String role) {
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

    /**
     * Returns the last login date formatted as "yyyy-MM-dd".
     * If no login date is set, returns "Never logged in".
     *
     * @return formatted last login date
     */
    public String getFormattedLastLoginDate() {
        if (lastLoginDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(lastLoginDate);
        } else {
            return "Never logged in";
        }
    }

    /**
     * Returns a string representation of the user.
     *
     * @return user details as a string
     */
    @Override
    public String toString() {
        return username + " | " + role + " | " + email + " | Last Login: " + getFormattedLastLoginDate();
    }
}
