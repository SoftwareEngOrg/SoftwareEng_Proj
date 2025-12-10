package Service;
import Domain.User ;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * The FileUserRepository class manages user data, including authentication and user account operations.
 * It reads and writes user data from/to a file, handles adding new users, checking for existing users,
 * updating user details (like last login date), and unregistering users.
 */
public class FileUserRepository {


    // Path to the user data file
    public static String repoPath = "users.txt";

    /**
     * Finds a user by their username and password. If the credentials are correct,
     * it returns the corresponding User object, otherwise returns null.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the User object if credentials are correct, or null if not
     */

    public User findUser(String username, String password)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try(BufferedReader br = new BufferedReader(new FileReader(repoPath)))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                String[] parts = line.split(";");
                if(parts.length == 5)
                {
                    if(parts[0].equals(username) && parts[1].equals(password))
                    {
                        Date lastLoginDate = dateFormat.parse(parts[4]);
                        return new User(parts[0], parts[1], parts[2] , parts[3],lastLoginDate);
                    }
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Error reading users file.");
        }

        return null;
    }
    /**
     * Adds a new user to the user repository. If the username already exists, the user is not added.
     * The new user's role is set to "customer" by default.
     *
     * @param username the username of the new user
     * @param password the password of the new user
     * @param email the email of the new user
     * @param current the current date (used for the last login date)
     * @return true if the user was successfully added, false if the username already exists
     */
    public boolean addUser(String username, String password, String email,Date current) {

        if (isUsernameExists(username)) {
            System.out.println("Username already exists!");
            return false;
        }

        String role = "customer";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(repoPath, true))) {

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(current);

            bw.write(username + ";" + password + ";" + role + ";" + email+";"+formattedDate);
            bw.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error writing to users file.");
            return false;
        }
    }
    /**
     * Checks if a given username already exists in the repository.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    public boolean isUsernameExists(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(repoPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading users file.");
        }
        return false;
    }
    /**
     * Updates the last login date of a user in the repository.
     * This method finds the user by their username and password and updates their last login date
     * to the current date.
     *
     * @param foundUser the user whose last login date will be updated
     */
    public void updateDate(User foundUser) {
        StringBuilder fileContent = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
        try (BufferedReader br = new BufferedReader(new FileReader(repoPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    if (parts[0].equals(foundUser.getUsername()) && parts[1].equals(foundUser.getPassword())) {
                        parts[4] = currentDate;
                        line = String.join(";", parts);
                    }
                }
                fileContent.append(line).append("\n");
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(repoPath))) {
                bw.write(fileContent.toString());
            }

        } catch (IOException e) {
            System.out.println("Error processing the file.");
        }
    }
    /**
     * Retrieves all users from the repository.
     *
     * @return a list of all users in the repository
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try (BufferedReader br = new BufferedReader(new FileReader(repoPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    Date lastLoginDate = dateFormat.parse(parts[4]);
                    User user = new User(parts[0], parts[1], parts[2], parts[3], lastLoginDate);
                    users.add(user);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading users file.");
        }
        return users;
    }
    /**
     * Unregisters a user by their username. If the user is found, they are removed from the repository.
     *
     * @param username the username of the user to unregister
     * @return true if the user was unregistered, false if the user was not found
     */
    public boolean unregisterUserByUsername(String username) {
        List<User> users = getAllUsers();
        boolean userFound = false;
        StringBuilder fileContent = new StringBuilder();
        for (User user : users) {
            if (!user.getUsername().equals(username)) {
                fileContent.append(user.getUsername()).append(";")
                        .append(user.getPassword()).append(";")
                        .append(user.getRole()).append(";")
                        .append(user.getEmail()).append(";")
                        .append(new SimpleDateFormat("yyyy-MM-dd").format(user.getLastLoginDate()))
                        .append("\n");
            } else {
                userFound = true;
            }
        }

        if (userFound) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(repoPath))) {
                bw.write(fileContent.toString());
                System.out.println("User " + username + " has been unregistered.");
                return true;
            } catch (IOException e) {
                System.out.println("Error updating the users file.");
            }
        } else {
            System.out.println("User " + username + " not found.");
        }

        return false;
    }
    /**
     * Unregisters all users who are in the provided list of inactive users.
     *
     * @param inActiveUsers the list of users to unregister
     * @return true if all inactive users were unregistered, false if an error occurred
     */
    public boolean unregisterAllUsers(List<User> inActiveUsers) {
        List<User> allUsers = getAllUsers();
        StringBuilder fileContent = new StringBuilder();
        Set<String> inactiveUsernames = inActiveUsers.stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());

        for (User user : allUsers) {
            if (!inactiveUsernames.contains(user.getUsername())) {
                fileContent.append(user.getUsername()).append(";")
                        .append(user.getPassword()).append(";")
                        .append(user.getRole()).append(";")
                        .append(user.getEmail()).append(";")
                        .append(new SimpleDateFormat("yyyy-MM-dd").format(user.getLastLoginDate()))
                        .append("\n");
            } else {
                System.out.println("User " + user.getUsername() + " is inactive and has been unregistered.");
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(repoPath))) {
            bw.write(fileContent.toString());
            System.out.println("All inactive users have been unregistered.");
            return true;
        } catch (IOException e) {
            System.out.println("Error updating the users file.");
            return false;
        }
    }
    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to find
     * @return the User object if found, or null if not found
     */
    public User findUserByUsername(String username) {
        List<User> allUsers = getAllUsers();

        return allUsers.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

}
