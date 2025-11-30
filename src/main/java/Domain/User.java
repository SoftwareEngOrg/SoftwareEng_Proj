package Domain;

public class User {

    private String username;
    private String password;
    private String role;
    private String email;

    public User(String username , String password , String role , String email)
    {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
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
        return (username + " | " + password + " | " + role + " | " + email);
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
