package Service;
import Domain.User ;
import java.io.*;

public class FileUserRepository {

    public static String repoPath = "users.txt";


    public User findUser(String username, String password)
    {
        try(BufferedReader br = new BufferedReader(new FileReader(repoPath)))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                String[] parts = line.split(";");
                if(parts.length == 4)
                {
                    if(parts[0].equals(username) && parts[1].equals(password))
                    {
                        return new User(parts[0], parts[1], parts[2] , parts[3]);
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
}
