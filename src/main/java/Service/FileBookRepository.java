package Service;
import Domain.Book ;
import Domain.MediaItem;

import java.io.*;
import java.util.*;

public class FileBookRepository {

    public static String repoPath = "books.txt";
    private List<Book> cachedBooks = new ArrayList<>();

    public FileBookRepository() {
        loadBooksFromFile(); // Load once when object is created
    }

    public void saveBook(Book b)
    {
        try(PrintWriter pw = new PrintWriter(new FileWriter(repoPath, true)))
        {
            pw.println(b.getTitle() + ";" + b.getAuthor() + ";" + b.getIsbn() + ";" + b.isAvailable());
            cachedBooks.add(b);
        }

        catch(Exception e)
        {
            System.out.println("Error writing to books file.");
        }
    }

    private void loadBooksFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(repoPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length == 4) {
                    Book book = new Book(p[0], p[1], p[2]);
                    book.setAvailable(Boolean.parseBoolean(p[3]));
                    cachedBooks.add(book);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading books file.");
        }
    }
    public List<Book> findAllBooks() {
        return new ArrayList<>(cachedBooks);
    }

    public void updateBooks(MediaItem item) {
        for (Book b : cachedBooks) {
            if(b.getTitle().equals(item.getTitle()) && b.getAuthor().equals(item.getAuthor()))
            {
                b.setAvailable(item.isAvailable());
            }
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(repoPath))) {
            for (Book b : cachedBooks) {
                pw.println(b.getTitle() + ";" + b.getAuthor() + ";" + b.getIsbn() + ";" + b.isAvailable());
            }
        } catch (Exception e) {
            System.out.println("Error saving file");
        }
    }

}
