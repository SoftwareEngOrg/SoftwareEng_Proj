package Service;
import Domain.Book ;
import Domain.MediaCopy;
import Domain.MediaItem;

import java.io.*;
import java.util.*;
/**
 * The FileBookRepository class is responsible for managing the persistence of Book objects
 * in a text file. It supports operations like loading books from a file, saving new books,
 * updating book details, and managing the availability of books.
 */
public class FileBookRepository {

    static FileBookRepository instance;
    private static final String FILE_PATH = "books.txt";
    public static  String repoPath = FILE_PATH;
    private static List<Book> cachedBooks = new ArrayList<>();


    /**
     * Private constructor to prevent instantiation from outside the class.
     * Initializes the repository by loading the books from the file.
     */
    private  FileBookRepository() {
        loadBooksFromFile();
    }
    /**
     * Returns the file path where the books are stored.
     *
     * @return the file path as a string
     */
    private String getFilePath() {
        return (repoPath != null && !repoPath.isEmpty()) ? repoPath : FILE_PATH;
    }
    /**
     * Returns the instance of the FileBookRepository. Ensures that only one instance of this
     * class is created (Singleton Pattern).
     *
     * @return the single instance of the FileBookRepository
     */
    public static synchronized FileBookRepository getInstance() {
        if (instance == null) {
            instance = new FileBookRepository();
        }
        return instance;
    }

    /*public static void saveBook(Book book, int numberOfCopies) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(repoPath, true))) {

            pw.println(book.getTitle() + ";" + book.getAuthor() + ";" + book.getIsbn() + ";" + true);
            cachedBooks.add(book);
        } catch (Exception e) {
            System.out.println("Error writing to books file: " + e.getMessage());
        }

        FileMediaCopyRepository.getInstance().addCopiesByBookIsbn(book.getIsbn(), numberOfCopies, true);
    }*/
    /**
     * Saves a new book to the file and updates the cache of books.
     * Also adds the specified number of copies to the media copy repository.
     *
     * @param book the book to be saved
     * @param numberOfCopies the number of copies of the book to be added
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
    /**
     * Loads the list of books from the file into the cache.
     */
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
     * Returns all books in the repository.
     *
     * @return a list of all books
     */
    public List<Book> findAllBooks() {
        return new ArrayList<>(cachedBooks);
    }
    /**
     * Updates the details of a specific book in the repository and saves the updated information.
     *
     * @param item the MediaItem (Book) whose details are to be updated
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
    /**
     * Saves all books in the cache back to the file.
     */
    private void saveAllBooksToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getFilePath()))) {
            for (Book b : cachedBooks) {
                pw.println(b.getTitle() + ";" + b.getAuthor() + ";" + b.getIsbn() + ";" + b.isAvailable());
            }
        } catch (Exception e) {
            System.out.println("Error saving books file");
        }
    }
    /**
     * Reloads the list of books from the file into the cache.
     */
    public void reloadBooks() {
        loadBooksFromFile();
    }
    /**
     * Finds a book by its ISBN.
     *
     * @param isbn the ISBN of the book
     * @return the Book object if found, or null if not found
     */
    public Book findByIsbn(String isbn) {
        return findAllBooks().stream()
                .filter(book -> book.getIsbn().equalsIgnoreCase(isbn.trim()))
                .findFirst()
                .orElse(null);
    }
    /**
     * Updates the availability of a book based on the available copies in the media copy repository.
     * If the availability status of the book changes (from unavailable to available),
     * it will notify the waitlist for that book.
     *
     * @param isbn the ISBN of the book to update
     */
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
