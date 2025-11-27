package Service;
import Domain.Book ;
import java.io.*;
import java.util.*;

public class FileBookRepository {

    public static String repoPath = "books.txt";


    public void saveBook(Book b)
    {
        try(PrintWriter pw = new PrintWriter(new FileWriter(repoPath, true)))
        {
            pw.println(b.getTitle() + ";" + b.getAuthor() + ";" + b.getIsbn());
        }

        catch(Exception e)
        {
            System.out.println("Error writing to books file.");
        }
    }

    public List<Book> searchBooks (String type , String value)
    {
        List<Book> result = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(repoPath)))
        {
            String line ;
            while((line = br.readLine()) != null)
            {
                String[] p = line.split(";");
                if(p.length == 3)
                {
                    Book b = new Book(p[0], p[1], p[2]);

                    if(type.equals("title") && p[0].contains(value))
                    {
                        result.add(b);

                    }

                    if(type.equals("author") && p[1].contains(value))
                    {
                        result.add(b);

                    }

                    if(type.equals("isbn") && p[2].equals(value))
                    {
                        result.add(b);

                    }
                }
            }

        }

        catch (Exception e)
        {
            System.out.println("Error reading books file.");
        }
        return result ;
    }

}
