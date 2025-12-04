package Service;
import Domain.Book ;
import Domain.MediaCopy;
import Domain.MediaItem;

import java.io.*;
import java.util.*;

public class FileBookRepository {

    private static FileBookRepository instance;
    public static String repoPath = "books.txt";
    private List<Book> cachedBooks = new ArrayList<>();


    private  FileBookRepository() {
        loadBooksFromFile();
    }

    public static synchronized FileBookRepository getInstance() {
        if (instance == null) {
            instance = new FileBookRepository();
        }
        return instance;
    }

    public void saveBook(Book book, int numberOfCopies) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(repoPath, true))) {

            pw.println(book.getTitle() + ";" + book.getAuthor() + ";" + book.getIsbn() + ";" + true);
            cachedBooks.add(book);
        } catch (Exception e) {
            System.out.println("Error writing to books file: " + e.getMessage());
        }

        FileMediaCopyRepository.getInstance().addCopiesByBookIsbn(book.getIsbn(), numberOfCopies, true);
    }

    private void loadBooksFromFile() {
        cachedBooks.clear();
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
            if (b.getIsbn().equals(item.getIsbnOrId())) {
                b.setAvailable(item.isAvailable());
                b.setTitle(item.getTitle());
                b.setAuthor(item.getAuthor());
            }
        }
        saveAllBooksToFile();
    }

    private void saveAllBooksToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(repoPath))) {
            for (Book b : cachedBooks) {
                pw.println(b.getTitle() + ";" + b.getAuthor() + ";" + b.getIsbn() + ";" + b.isAvailable());
            }
        } catch (Exception e) {
            System.out.println("Error saving books file");
        }
    }

    public void reloadBooks() {
        loadBooksFromFile();
    }

    public Book findByIsbn(String isbn) {
        return findAllBooks().stream()
                .filter(book -> book.getIsbn().equalsIgnoreCase(isbn.trim()))
                .findFirst()
                .orElse(null);
    }

    public void updateBookAvailability(String isbn) {
        Book book = findByIsbn(isbn);
        if (book != null) {

            int availableCopies = FileMediaCopyRepository.getInstance()
                    .getAvailableCopiesCount(isbn);

            boolean wasAvailable = book.isAvailable();
            boolean nowAvailable = (availableCopies > 0);

            book.setAvailable(nowAvailable);
            saveAllBooksToFile();


            if (!wasAvailable && nowAvailable) {
                System.out.println("Book is now available - notifying waitlist...");
            }
        }
    }

}
