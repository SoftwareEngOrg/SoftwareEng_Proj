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
                if(parts.length == 3)
                {
                    if(parts[0].equals(username) && parts[1].equals(password))
                    {
                        return new User(parts[0], parts[1], parts[2]);
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

}
