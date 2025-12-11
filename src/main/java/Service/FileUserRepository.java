package Service;

import Domain.User;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository class for managing users stored in a file.
 * Provides methods for adding, finding, updating, and unregistering users.
 */
public class FileUserRepository {

    private static FileUserRepository instance;
    private static final String FILE_PATH = "users.txt";
    public static String repoPath = FILE_PATH;

    /**
     * Returns the file path of the user repository.
     *
     * @return the file path
     */
    private String getFilePath() {
        return (repoPath != null && !repoPath.isEmpty()) ? repoPath : FILE_PATH;
    }

    /**
     * Sets a new file path for the repository and resets the instance.
     *
     * @param newPath the new file path
     */
    public static void setRepoPath(String newPath) {
        repoPath = newPath;
        instance = null;
    }

    /**
     * Returns the singleton instance of FileUserRepository.
     *
     * @return the repository instance
     */
    public static synchronized FileUserRepository getInstance() {
        if (instance == null) {
            instance = new FileUserRepository();
        }
        return instance;
    }

    /**
     * Resets the singleton instance.
     */
    public static void reset() {
        instance = null;
    }

    /**
     * Finds a user by username and password.
     *
     * @param username the username
     * @param password the password
     * @return the user if found, otherwise null
     */
    public User findUser(String username, String password) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try (BufferedReader br = new BufferedReader(new FileReader(repoPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    if (parts[0].equals(username) && parts[1].equals(password)) {
                        Date lastLoginDate = dateFormat.parse(parts[4]);
                        return new User(parts[0], parts[1], parts[2], parts[3], lastLoginDate);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading users file.");
        }
        return null;
    }

    /**
     * Adds a new user to the repository.
     *
     * @param username the username
     * @param password the password
     * @param email the user email
     * @param current the current date
     * @return true if added successfully, false if username exists or error occurs
     */
    public boolean addUser(String username, String password, String email, Date current) {
        if (isUsernameExists(username)) {
            System.out.println("Username already exists!");
            return false;
        }

        String role = "customer";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(repoPath, true))) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(current);

            bw.write(username + ";" + password + ";" + role + ";" + email + ";" + formattedDate);
            bw.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error writing to users file.");
            return false;
        }
    }

    /**
     * Checks if a username already exists in the repository.
     *
     * @param username the username to check
     * @return true if exists, false otherwise
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
     * Updates the last login date of a user.
     *
     * @param foundUser the user to update
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
     * Returns a list of all users.
     *
     * @return list of users
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
     * Unregisters a user by username.
     *
     * @param username the username to remove
     * @return true if user was removed, false otherwise
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
     * Unregisters all inactive users.
     *
     * @param inActiveUsers list of inactive users
     * @return true if operation succeeded, false otherwise
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
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return the user if found, otherwise null
     */
    public User findUserByUsername(String username) {
        List<User> allUsers = getAllUsers();
        return allUsers.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
