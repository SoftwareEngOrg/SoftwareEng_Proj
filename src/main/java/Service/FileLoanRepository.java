package Service;

import Domain.CD;
import Domain.Loan;
import Domain.MediaItem;
import Domain.User;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/**
 * The FileLoanRepository class is responsible for managing loans of media items (books and CDs).
 * It supports operations such as borrowing items, returning items, and retrieving loan details.
 * Data is persisted in a text file (`loans.txt`), and it handles the availability of items
 * as they are loaned out and returned.
 */
public class FileLoanRepository {
    public static  String FILE_PATH = "loans.txt";
    public static String repoPath = FILE_PATH;
    private final List<Loan> loans = new ArrayList<>();
    private final FileBookRepository bookRepository; // Reuse cached books
    private final FileCDRepository cdRepository;
    /**
     * Returns the file path where loan data is stored.
     *
     * @return the file path as a string
     */
    private String getFilePath() {
        return (repoPath != null && !repoPath.isEmpty()) ? repoPath : FILE_PATH;
    }
    /**
     * Constructs a FileLoanRepository instance, initializing the book and CD repositories.
     * It also loads existing loan data from the loan file into memory.
     */
    public FileLoanRepository() {
        this.bookRepository = FileBookRepository.getInstance();
        this.cdRepository = FileCDRepository.getInstance();
        loadLoans();
    }
    /**
     * Borrows a media item (book or CD) for a user. A unique loan ID is generated, and the
     * item's borrowing date and due date are set.
     *
     * @param user the user borrowing the item
     * @param item the media item being borrowed
     * @return the Loan object representing the loan transaction
     */
    public Loan borrowItem(User user, MediaItem item) {


        String loanId = UUID.randomUUID().toString().substring(0, 8);
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(item.getBorrowingPeriodDays());

        Loan loan = new Loan(loanId, user, item, borrowDate);

        loans.add(loan);
        saveToFile();

        return loan;
    }
    /**
     * Returns a borrowed item by its loan ID. The item's availability is updated,
     * and the loan is marked as returned.
     *
     * @param loanId the ID of the loan
     * @param returnDate the date the item was returned
     * @return true if the item was returned successfully, false otherwise
     */
    public boolean returnItem(String loanId, LocalDate returnDate) {
        for (Loan loan : loans) {
            if (loan.getLoanId().equals(loanId) && loan.getReturnDate() == null) {
                loan.returnItem(returnDate != null ? returnDate : LocalDate.now());

                MediaItem item = loan.getMediaItem();
                item.setAvailable(true);
                if (item instanceof Domain.Book) {
                    bookRepository.updateBooks(item);
                } else if (item instanceof CD) {
                    List<CD> allCDs = cdRepository.findAllCDs();
                    for(CD cd:allCDs)
                    {
                        if(cd.getIsbn().equals(((CD) item).getIsbn()))
                            cd.setAvailable(true);
                    }
                    cdRepository.updateAll(allCDs);
                }
                saveToFile();
                return true;
            }
        }
        return false;
    }
    /**
     * Retrieves a list of active loans for a specific user (loans that have not been returned).
     *
     * @param username the username of the user
     * @return a list of active loans for the user
     */
    public List<Loan> getActiveLoansForUser(String username)
    {
        return loans.stream()
                .filter(loan -> loan.getUser().getUsername().equals(username))
                .filter(loan -> loan.getReturnDate() == null)
                .toList();
    }
    /**
     * Retrieves a list of all active loans in the system (loans that have not been returned).
     *
     * @return a list of all active loans
     */
    public List<Loan> getAllActiveLoans() {
        return loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .toList();
    }
    /**
     * Retrieves a list of overdue loans based on the current date.
     *
     * @param currentDate the current date used to check for overdue loans
     * @return a list of overdue loans
     */
    public List<Loan> getOverdueLoans(LocalDate currentDate) {
        return loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .filter(loan -> loan.isOverdue(currentDate))
                .toList();
    }
    /**
     * Finds a loan by its loan ID.
     *
     * @param loanId the ID of the loan
     * @return the Loan object if found, or null if not found
     */
    public Loan findLoanById(String loanId) {
        return loans.stream()
                .filter(l -> l.getLoanId().equals(loanId))
                .findFirst()
                .orElse(null);
    }
    /**
     * Saves all current loan data to the loan file, overwriting the existing file.
     */
    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getFilePath()))) {
            for (Loan loan : loans) {
                String returnDateStr = loan.getReturnDate() == null ? "NULL" : loan.getReturnDate().toString();
                String line = String.format("%s;%s;%s;%s;%s",
                        loan.getLoanId(),
                        loan.getUser().getUsername(),
                        loan.getMediaItem().getIsbnOrId(),
                        loan.getBorrowDate(),
                        returnDateStr
                );
                pw.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error saving loans: " + e.getMessage());
        }
    }
    /**
     * Loads all loan data from the loan file into memory.
     */
    private void loadLoans()
    {
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

                if (user != null && item != null) {
                    Loan loan = new Loan(loanId, user, item, borrowDate);
                    if (returnDate != null) {
                        loan.returnItem(returnDate);
                    } else {
                        item.setAvailable(false);
                    }
                    loans.add(loan);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading loans: " + e.getMessage());
        }
    }
    /**
     * Finds a user by their username. This is a mock method for demonstration.
     *
     * @param username the username of the user
     * @return a User object with the given username
     */
    private User findUserByUsername(String username) {

        return new User(username, "temp", username.equals("admin") ? "admin" : "customer");
    }

    /**
     * Finds a media item (book or CD) by its ID (ISBN).
     *
     * @param id the ID (ISBN) of the media item
     * @return the MediaItem if found, or null if not found
     */
    private MediaItem findMediaItemById(String id) {
        MediaItem book = bookRepository.findAllBooks().stream()
                .filter(b -> b.getIsbn().equals(id))
                .findFirst()
                .orElse(null);

        if (book != null) {
            return book;
        }


        return cdRepository.findAllCDs().stream()
                .filter(cd -> cd.getIsbn().equals(id))
                .findFirst()
                .orElse(null);
    }

}
