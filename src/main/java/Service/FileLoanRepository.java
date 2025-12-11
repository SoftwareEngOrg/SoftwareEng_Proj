package Service;

import Domain.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Repository class for managing loans stored in a file.
 * Provides methods to borrow, return, and query loans.
 */
public class FileLoanRepository {
    static FileLoanRepository instance;
    public static String FILE_PATH = "loans.txt";
    public static String repoPath = FILE_PATH;
    private final List<Loan> loans = new ArrayList<>();
    private final FileBookRepository bookRepository; // Reuse cached books
    private final FileCDRepository cdRepository;

    /**
     * Returns the file path for the repository.
     *
     * @return the file path
     */
    private String getFilePath() {
        return (repoPath != null && !repoPath.isEmpty()) ? repoPath : FILE_PATH;
    }

    /**
     * Sets a new file path and resets the repository instance.
     *
     * @param newPath the new path
     */
    public static void setRepoPath(String newPath) {
        repoPath = newPath;
        instance = null;
    }

    /**
     * Returns the singleton instance of FileLoanRepository.
     *
     * @return the repository instance
     */
    public static synchronized FileLoanRepository getInstance() {
        if (instance == null) {
            instance = new FileLoanRepository();
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
     * Initializes the repository by loading loans from file.
     */
    public FileLoanRepository() {
        this.bookRepository = FileBookRepository.getInstance();
        this.cdRepository = FileCDRepository.getInstance();
        loadLoans();
    }

    /**
     * Borrows a media item for a user and creates a new loan.
     *
     * @param user the user borrowing the item
     * @param item the media item to borrow
     * @return the created Loan
     * @throws IllegalArgumentException if user or item is null
     * @throws IllegalStateException if item is not available
     */
    public synchronized Loan borrowItem(User user, MediaItem item) {
        if (user == null) throw new IllegalArgumentException("user is null");
        if (item == null) throw new IllegalArgumentException("item is null");
        if (!item.isAvailable()) throw new IllegalStateException("Item is not available");

        Loan loan = new Loan(UUID.randomUUID().toString(), user, item, LocalDate.now());
        this.loans.add(loan);
        saveToFile();
        return loan;
    }

    /**
     * Returns a borrowed item by loan ID.
     *
     * @param loanId the loan ID
     * @param returnDate the return date (null = today)
     * @return true if return successful, false otherwise
     */
    public synchronized boolean returnItem(String loanId, LocalDate returnDate) {
        Loan loan = findLoanById(loanId);
        if (loan == null || loan.getReturnDate() != null) return false;

        loan.returnItem(returnDate != null ? returnDate : LocalDate.now());
        saveToFile();
        return true;
    }

    /**
     * Returns all active loans for a specific user.
     *
     * @param username the user's username
     * @return list of active loans
     */
    public List<Loan> getActiveLoansForUser(String username) {
        return loans.stream()
                .filter(loan -> loan.getUser().getUsername().equals(username))
                .filter(loan -> loan.getReturnDate() == null)
                .toList();
    }

    /**
     * Returns all active loans.
     *
     * @return list of active loans
     */
    public List<Loan> getAllActiveLoans() {
        return loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .toList();
    }

    /**
     * Returns all overdue loans as of the specified date.
     *
     * @param currentDate the date to check overdue
     * @return list of overdue loans
     */
    public List<Loan> getOverdueLoans(LocalDate currentDate) {
        return loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .filter(loan -> loan.isOverdue(currentDate))
                .toList();
    }

    /**
     * Finds a loan by its ID.
     *
     * @param loanId the loan ID
     * @return the Loan if found, null otherwise
     */
    public Loan findLoanById(String loanId) {
        return loans.stream()
                .filter(l -> l.getLoanId().equals(loanId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Saves all loans to the file.
     */
    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getFilePath()))) {
            for (Loan loan : loans) {
                String returnDateStr = loan.getReturnDate() == null ? "NULL" : loan.getReturnDate().toString();
                pw.println(String.format("%s;%s;%s;%s;%s",
                        loan.getLoanId(),
                        loan.getUser().getUsername(),
                        loan.getMediaItem().getIsbnOrId(),
                        loan.getBorrowDate(),
                        returnDateStr));
            }
        } catch (IOException e) {
            System.out.println("Error saving loans: " + e.getMessage());
        }
    }

    /**
     * Loads all loans from the file into memory.
     */
    private void loadLoans() {
        loans.clear();
        File file = new File(getFilePath());
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length != 5) continue;

                String loanId = p[0];
                String username = p[1];
                String itemId = p[2];
                LocalDate borrowDate = LocalDate.parse(p[3]);
                LocalDate returnDate = p[4].equals("NULL") ? null : LocalDate.parse(p[4]);

                User user = findUserByUsername(username);
                MediaItem item = findMediaItemById(itemId);

                if (item != null) {
                    Loan loan = new Loan(loanId, user, item, borrowDate);
                    if (returnDate != null) loan.returnItem(returnDate);
                    else item.setAvailable(false);
                    loans.add(loan);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading loans: " + e.getMessage());
        }
    }

    /**
     * Finds a User object by username (dummy for repo integration).
     */
    private User findUserByUsername(String username) {
        return new User(username, "temp", username.equals("admin") ? "admin" : "customer");
    }

    /**
     * Finds a MediaItem by its ID.
     */
    private MediaItem findMediaItemById(String id) {
        MediaItem book = bookRepository.findAllBooks().stream()
                .filter(b -> b.getIsbn().equals(id))
                .findFirst().orElse(null);
        if (book != null) return book;

        return cdRepository.findAllCDs().stream()
                .filter(cd -> cd.getIsbn().equals(id))
                .findFirst().orElse(null);
    }
}
