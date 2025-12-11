package Service;

import Domain.Book;
import Domain.MediaItem;

import java.io.*;
import java.util.*;

/**
 * Repository class for managing Books stored in a file.
 * Provides methods to save, update, query, and manage availability of books.
 */
public class FileBookRepository {

    static FileBookRepository instance;
    private static final String FILE_PATH = "books.txt";
    public static String repoPath = FILE_PATH;
    private static List<Book> cachedBooks = new ArrayList<>();

    private FileBookRepository() {
        loadBooksFromFile();
    }

    private String getFilePath() {
        return (repoPath != null && !repoPath.isEmpty()) ? repoPath : FILE_PATH;
    }

    /**
     * Sets a new file path for the repository.
     *
     * @param newPath the new repository path
     */
    public static void setRepoPath(String newPath) {
        repoPath = newPath;
        instance = null;
        cachedBooks.clear();
    }

    /**
     * Returns the singleton instance of FileBookRepository.
     *
     * @return the repository instance
     */
    public static synchronized FileBookRepository getInstance() {
        if (instance == null) {
            instance = new FileBookRepository();
        }
        return instance;
    }

    /**
     * Resets the singleton instance.
     */
    public static void reset() {
        instance = null;
        cachedBooks.clear();
    }

    /**
     * Saves a book to the file and adds its copies to the media copy repository.
     *
     * @param book the book to save
     * @param numberOfCopies number of copies to add
     */
    public static void saveBook(Book book, int numberOfCopies) {
        FileBookRepository instance = getInstance();
        try (PrintWriter pw = new PrintWriter(new FileWriter(instance.getFilePath(), true))) {
            pw.println(book.getTitle() + ";" + book.getAuthor() + ";" + book.getIsbn() + ";" + true);
            cachedBooks.add(book);
        } catch (Exception e) {
            System.out.println("Error writing to books file: " + e.getMessage());
        }

        FileMediaCopyRepository.getInstance().addCopiesByBookIsbn(book.getIsbn(), numberOfCopies, true);
    }

    /** Loads books from file into cache. */
    private void loadBooksFromFile() {
        cachedBooks.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(getFilePath()))) {
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

    /**
     * Returns a list of all books in the repository.
     *
     * @return list of books
     */
    public List<Book> findAllBooks() {
        return new ArrayList<>(cachedBooks);
    }

    /**
     * Updates the information of a book.
     *
     * @param item the book or media item to update
     */
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

    /** Saves all cached books to file. */
    private void saveAllBooksToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getFilePath()))) {
            for (Book b : cachedBooks) {
                pw.println(b.getTitle() + ";" + b.getAuthor() + ";" + b.getIsbn() + ";" + b.isAvailable());
            }
        } catch (Exception e) {
            System.out.println("Error saving books file");
        }
    }

    /** Reloads books from file into cache. */
    public void reloadBooks() {
        loadBooksFromFile();
    }

    /**
     * Finds a book by ISBN.
     *
     * @param isbn the ISBN to search for
     * @return the book if found, otherwise null
     */
    public Book findByIsbn(String isbn) {
        return findAllBooks().stream()
                .filter(book -> book.getIsbn().equalsIgnoreCase(isbn.trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates the availability status of a book based on available copies.
     *
     * @param isbn the ISBN of the book
     */
    public void updateBookAvailability(String isbn) {
        Book book = findByIsbn(isbn);
        if (book != null) {
            int availableCopies = FileMediaCopyRepository.getInstance().getAvailableCopiesCount(isbn);
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
