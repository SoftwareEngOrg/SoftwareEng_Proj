package Service;
import Domain.User ;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FileUserRepository {

    public static String repoPath = "users.txt";


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


    public boolean addUser(String username, String password, String email) {

        if (isUsernameExists(username)) {
            System.out.println("Username already exists!");
            return false;
        }

        String role = "customer";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(repoPath, true))) {
            bw.write(username + ";" + password + ";" + role + ";" + email);
            bw.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error writing to users file.");
            return false;
        }
    }



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
            e.printStackTrace();
        }
    }
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try (BufferedReader br = new BufferedReader(new FileReader(repoPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) { // Ensure there are enough parts (username, password, role, email, lastLogin)
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
                userFound = true; // Mark if the user was found and removed
            }
        }

        if (userFound) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(repoPath))) {
                bw.write(fileContent.toString());
                System.out.println("User " + username + " has been unregistered.");
                return true;
            } catch (IOException e) {
                System.out.println("Error updating the users file.");
                e.printStackTrace();
            }
        } else {
            System.out.println("User " + username + " not found.");
        }

        return false;
    }

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
            e.printStackTrace();
            return false;
        }
    }

}
