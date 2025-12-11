package Domain;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a user in the library system.
 * Each user has a username, password, role, email, and an optional last login date.
 *
 * <p>Example usage:</p>
 * <pre><code>
 * User user = new User("john", "1234", "ADMIN");
 * System.out.println(user.getUsername());
 * </code></pre>
 *
 * @since 1.0
 */
public class User {

    private String username;
    private String password;
    private String role;
    private String email;
    private Date lastLoginDate;

    /**
     * Creates a User with all fields specified, including last login date.
     *
     * @param username      the username
     * @param password      the password
     * @param role          the user's role (e.g., Admin, Librarian, Member)
     * @param email         the user's email
     * @param lastLoginDate the user's last login timestamp
     */
    public User(String username, String password, String role, String email, Date lastLoginDate) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * Creates a User without email and login date.
     *
     * @param username the username
     * @param password the password
     * @param role     the user's role
     */
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Returns the user's password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the user's role.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the user's email.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email the new email value
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the date of the user's last login.
     *
     * @return the last login date, or null if never logged in
     */
    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * Updates the last login date.
     *
     * @param lastLoginDate the new login timestamp
     */
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * Returns the last login date formatted as YYYY-MM-DD.
     *
     * @return a formatted date string or "Never logged in"
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
     * Returns a formatted string including user information.
     *
     * @return a string representation of the user
     */
    @Override
    public String toString() {
        return username + " | " + password + " | " + role + " | " + email +
                "| Last Login: " + getFormattedLastLoginDate();
    }
}
