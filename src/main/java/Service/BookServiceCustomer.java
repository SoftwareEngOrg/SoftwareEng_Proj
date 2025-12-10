package Service;

import Domain.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Service class responsible for managing book and media item loans for a customer.
 * This class handles borrowing and returning media items (Books, CDs), managing
 * fines, and interacting with the waitlist.
 *
 * Authors: Yahya, Ahmad
 */
public class BookServiceCustomer extends BookService{
    public final  static String SEPARATOR = "--------------------------------------";
    private  FileLoanRepository loanRepository = new FileLoanRepository();
    private  FileBookRepository fileBook = FileBookRepository.getInstance();
    private User currentUser;
    private Map<String, List<User>> waitList = new HashMap<>();
    private FileCDRepository fileCD = FileCDRepository.getInstance();
    private String emailUser;
    private String emailPass;
    private static BookServiceCustomer instance;
    /**
     * Sets the current user for the service.
     *
     * @param user the user who will perform actions like borrowing or returning items
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    /**
     * Constructor to initialize the BookServiceCustomer with email configuration.
     *
     * @param emailUser the email address used for notifications
     * @param emailPass the email password for sending notifications
     */
    public BookServiceCustomer(String emailUser, String emailPass) {
        this.emailUser = emailUser;
        this.emailPass = emailPass;
    }
    /**
     * Default constructor for BookServiceCustomer.
     */
    public BookServiceCustomer() {}
    /**
     * Sets the email configuration for sending notifications.
     *
     * @param emailUser the email address
     * @param emailPass the email password
     */
    public void setEmailConfig(String emailUser, String emailPass) {
        this.emailUser = emailUser;
        this.emailPass = emailPass;
    }
    /**
     * Borrows a media item (Book or CD) for the current user.
     * This method checks for item availability, overdue fines, and other constraints.
     * If the item is unavailable, the user is added to a waitlist.
     *
     * @param isbn the ISBN of the item to borrow
     * @return true if the borrowing was successful, false otherwise
     */
    public boolean borrowMediaItem(String isbn) {

        if (isbn == null || isbn.trim().isEmpty()) {
            System.out.println("Error: ISBN is null or empty.");
            return false;
        }

        if (currentUser == null) {
            System.out.println("Error: User not logged in.");
            return false;
        }

        MediaItem item = findMediaByIsbn(isbn);
        if (item == null) {
            System.out.println("Item with ISBN '" + isbn + "' not found.");
            return false;
        }


        List<MediaCopy> availableCopies = FileMediaCopyRepository.getInstance()
                .getCopiesByIsbn(isbn).stream()
                .filter(MediaCopy::isAvailable)
                .toList();


        if (availableCopies.isEmpty()) {
            System.out.println(item.getClass().getSimpleName() + " is currently borrowed.");
            System.out.println("We will notify you by email when it becomes available.");

            if (currentUser.getEmail() == null || currentUser.getEmail().isEmpty()) {
                System.out.println("Cannot add to waitlist: user email not set.");
                return false;
            }


            waitList.putIfAbsent(isbn, new ArrayList<>());
            if (!waitList.get(isbn).contains(currentUser)) {
                waitList.get(isbn).add(currentUser);
                System.out.println(" You have been added to the waitlist.");
            } else {
                System.out.println("You are already on the waitlist.");
            }


            EmailService es = new EmailService(this.emailUser, this.emailPass);
            CustomerObserver observer = new CustomerObserver(currentUser, es, this);
            BookInventory.getInstance().addObserver(observer);

            return false;
        }


        LocalDate today = LocalDate.now();
        List<Loan> activeLoans = loanRepository.getActiveLoansForUser(currentUser.getUsername());

        boolean hasOverdue = activeLoans.stream().anyMatch(loan -> loan.isOverdue(today));
        int totalFine = activeLoans.stream().mapToInt(loan -> loan.calculateFine(today)).sum();

        if (hasOverdue || totalFine > 0) {
            System.out.println("Cannot borrow: You have overdue items or unpaid fines (₪" + totalFine + ").");
            return false;
        }


        MediaCopy copyToBorrow = availableCopies.get(0);
        copyToBorrow.setAvailable(false);
        FileMediaCopyRepository.getInstance().saveToFile();


        FileBookRepository.getInstance().updateBookAvailability(isbn);


        Loan loan = loanRepository.borrowItem(currentUser, item);

        System.out.println(item.getClass().getSimpleName() + " borrowed successfully!");
        System.out.println("Loan ID: " + loan.getLoanId());
        System.out.println("Copy ID: " + copyToBorrow.getCopyId());
        System.out.println("Due date: " + loan.getDueDate());
        System.out.println("Borrowing period: " + item.getBorrowingPeriodDays() + " days");
        System.out.println("Fine per day if overdue: ₪" + item.getFinePerDay());

        return true;
    }
    /**
     * Returns a borrowed book or CD.
     * Checks if the item is overdue and calculates any fines.
     *
     * @param loanId the ID of the loan to return
     * @return true if the return was successful, false otherwise
     */
    public boolean returnBook(String loanId) {
        LocalDate today = LocalDate.now();
        Loan loan = loanRepository.findLoanById(loanId);

        if (loan == null || loan.getReturnDate() != null) {
            System.out.println("Invalid or already returned Loan ID.");
            return false;
        }

        if (!loan.getUser().getUsername().equals(currentUser.getUsername())) {
            System.out.println("This loan does not belong to you.");
            return false;
        }

        int fine = loan.calculateFine(today);

        if (fine > 0) {
            System.out.println("You have an overdue fine: ₪" + fine);
            return false;
        }

        MediaItem item = loan.getMediaItem();
        String isbn = item.getIsbnOrId();

        returnCopyByLoan(loan);

        loanRepository.returnItem(loanId, today);

        if (item instanceof Book) {
            FileBookRepository.getInstance().updateBookAvailability(isbn);
            BookInventory.getInstance().notifyBookReturned(isbn);
        } else if (item instanceof CD) {
            FileCDRepository.getInstance().updateCDAvailability(isbn);
            BookInventory.getInstance().notifyBookReturned(isbn);
        }

        System.out.println("Item returned on time. Thank you!");
        return true;
    }
    /**
     * Finalizes the return of a media item after fines are paid.
     * Updates availability of the item and notifies the inventory system.
     *
     * @param loanId the ID of the loan to finalize
     * @return true if the return was successful, false otherwise
     */
    public boolean completeReturn(String loanId) {
        LocalDate today = LocalDate.now();
        Loan loan = loanRepository.findLoanById(loanId);

        if (loan == null || loan.getReturnDate() != null) {
            System.out.println("Invalid or already returned Loan ID.");
            return false;
        }

        String isbn = loan.getMediaItem().getIsbnOrId();

        returnCopyByLoan(loan);

        loanRepository.returnItem(loanId, today);
        System.out.println("Fine paid. Item returned successfully!");

        MediaItem item = loan.getMediaItem();

        if (item instanceof Book) {
            FileBookRepository.getInstance().updateBookAvailability(isbn);
            BookInventory.getInstance().notifyBookReturned(isbn);

        } else if (item instanceof CD) {
            FileCDRepository.getInstance().updateCDAvailability(isbn);
            System.out.println("CD availability updated.");
        }

        return true;
    }
    /**
     * Returns a media copy by its loan details and updates its availability.
     *
     * @param loan the loan from which the media copy is returned
     */
    private void returnCopyByLoan(Loan loan) {
        String isbn = loan.getMediaItem().getIsbnOrId();
        List<MediaCopy> copies = FileMediaCopyRepository.getInstance().getCopiesByIsbn(isbn);

        for (MediaCopy copy : copies) {
            if (!copy.isAvailable()) {
                copy.setAvailable(true);
                FileMediaCopyRepository.getInstance().saveToFile();
                System.out.println("Copy " + copy.getCopyId() + " returned successfully.");
                return;
            }
        }

        System.out.println("Error: No borrowed copy found to return.");
    }

    /**
     * Views the current active loans for the logged-in user.
     * Displays both books and CDs and calculates overdue fines.
     */
    public void viewMyLoans() {
        if (currentUser == null) {
            System.out.println("Not logged in.");
            return;
        }

        LocalDate today = LocalDate.now();
        List<Loan> myLoans = loanRepository.getActiveLoansForUser(currentUser.getUsername());

        if (myLoans.isEmpty()) {
            System.out.println("You have no active loans.");
            return;
        }


        List<Loan> bookLoans = new ArrayList<>();
        List<Loan> cdLoans = new ArrayList<>();

        for (Loan loan : myLoans) {
            if (loan.getMediaItem() instanceof Book) {
                bookLoans.add(loan);
            } else if (loan.getMediaItem() instanceof CD) {
                cdLoans.add(loan);
            }
        }

        System.out.println("\n=== Your Active Loans ===");
        int totalFine = 0;


        if (!bookLoans.isEmpty()) {
            System.out.println("\n BOOK LOANS:");
            System.out.println(SEPARATOR);
            for (Loan loan : bookLoans) {
                int fine = loan.calculateFine(today);
                totalFine += fine;
                String status = loan.isOverdue(today) ? " (OVERDUE)" : "";
                System.out.printf("• %s → Due: %s | Loan ID: %s%s | Fine: ₪%d\n",
                        loan.getMediaItem().getTitle(),
                        loan.getDueDate(),
                        loan.getLoanId(),
                        status,
                        fine);
            }
            System.out.println(SEPARATOR);
        }


        if (!cdLoans.isEmpty()) {
            System.out.println("\n CD LOANS:");
            System.out.println(SEPARATOR);
            for (Loan loan : cdLoans) {
                int fine = loan.calculateFine(today);
                totalFine += fine;
                String status = loan.isOverdue(today) ? " (OVERDUE)" : "";
                System.out.printf("• %s → Due: %s | Loan ID: %s%s | Fine: ₪%d\n",
                        loan.getMediaItem().getTitle(),
                        loan.getDueDate(),
                        loan.getLoanId(),
                        status,
                        fine);
            }
            System.out.println(SEPARATOR);
        }

        if (totalFine > 0) {
            System.out.println("\n Total fine owed: ₪" + totalFine);
        }
    }
    /**
     * Retrieves all available books in the system.
     * It checks if there are available copies of each book and
     * returns a list of books that have at least one available copy.
     *
     * @return a list of available books
     */
    public List<Book> getAllAvailableBooks() {
        List<Book> allBooks = fileBook.findAllBooks();
        List<Book> availableBooks = new ArrayList<>();

        for (Book book : allBooks) {
            int availableCopies = FileMediaCopyRepository.getInstance()
                    .getAvailableCopiesCount(book.getIsbn());

            if (availableCopies > 0) {
                book.setAvailable(true);
                availableBooks.add(book);
            }
        }

        return availableBooks;
    }

    /**
     * Retrieves all available CDs in the system.
     * It checks if there are available copies of each CD and
     * returns a list of CDs that have at least one available copy.
     *
     * @return a list of available CDs
     */
    public List<CD> getAllAvailableCDs() {
        List<CD> allCDs = fileCD.findAllCDs();
        List<CD> availableCDs = new ArrayList<>();

        for (CD cd : allCDs) {
            int availableCopies = FileMediaCopyRepository.getInstance()
                    .getAvailableCopiesCount(cd.getIsbn());

            if (availableCopies > 0) {
                cd.setAvailable(true);
                availableCDs.add(cd);
            }
        }

        return availableCDs;
    }
    /**
     * Finds a media item (Book or CD) based on the provided ISBN.
     * It first attempts to find a book using the ISBN, and if not found,
     * it attempts to find a CD.
     *
     * @param isbn the ISBN of the media item to find
     * @return the media item (Book or CD) if found, or null if not found
     */
    public MediaItem findMediaByIsbn(String isbn) {
        Book book = fileBook.findByIsbn(isbn);
        if (book != null) {
            return book;
        }
        CD cd = fileCD.findByIsbn(isbn);
        if (cd != null) {
            return cd;
        }
        return null;
    }
    /**
     * Generates a detailed loan report for the current user.
     * It includes information about all active book and CD loans,
     * as well as any associated fines.
     *
     * @return a string containing the loan report for the user
     */
    public String generateLoanReport() {
        if (currentUser == null) {
            return "Not logged in.";
        }

        StringBuilder report = new StringBuilder();
        LocalDate today = LocalDate.now();
        List<Loan> myLoans = loanRepository.getActiveLoansForUser(currentUser.getUsername());

        report.append("=".repeat(50)).append("\n");
        report.append("        LIBRARY LOAN REPORT\n");
        report.append("=".repeat(50)).append("\n");
        report.append("User: ").append(currentUser.getUsername()).append("\n");
        report.append("Report Date: ").append(today).append("\n");
        report.append("=".repeat(50)).append("\n\n");

        if (myLoans.isEmpty()) {
            report.append("You have no active loans.\n");
            return report.toString();
        }


        List<Loan> bookLoans = new ArrayList<>();
        List<Loan> cdLoans = new ArrayList<>();

        for (Loan loan : myLoans) {
            if (loan.getMediaItem() instanceof Book) {
                bookLoans.add(loan);
            } else if (loan.getMediaItem() instanceof CD) {
                cdLoans.add(loan);
            }
        }

        int totalFine = 0;

        // Display Book Loans
        if (!bookLoans.isEmpty()) {
            report.append("BOOK LOANS:\n");
            report.append("-".repeat(50)).append("\n");
            for (Loan loan : bookLoans) {
                int fine = loan.calculateFine(today);
                totalFine += fine;
                String status = loan.isOverdue(today) ? " (OVERDUE)" : "";
                report.append(String.format("• %s\n", loan.getMediaItem().getTitle()));
                report.append(String.format("  Author: %s\n", loan.getMediaItem().getAuthor()));
                report.append(String.format("  Due Date: %s%s\n", loan.getDueDate(), status));
                report.append(String.format("  Loan ID: %s\n", loan.getLoanId()));
                report.append(String.format("  Fine: ₪%d\n", fine));
                report.append("\n");
            }
            report.append("-".repeat(50)).append("\n\n");
        }


        if (!cdLoans.isEmpty()) {
            report.append("CD LOANS:\n");
            report.append("-".repeat(50)).append("\n");
            for (Loan loan : cdLoans) {
                int fine = loan.calculateFine(today);
                totalFine += fine;
                String status = loan.isOverdue(today) ? " (OVERDUE)" : "";
                report.append(String.format("• %s\n", loan.getMediaItem().getTitle()));
                report.append(String.format("  Artist: %s\n", loan.getMediaItem().getAuthor()));
                report.append(String.format("  Due Date: %s%s\n", loan.getDueDate(), status));
                report.append(String.format("  Loan ID: %s\n", loan.getLoanId()));
                report.append(String.format("  Fine: ₪%d\n", fine));
                report.append("\n");
            }
            report.append("-".repeat(50)).append("\n\n");
        }

        if (totalFine > 0) {
            report.append("=".repeat(50)).append("\n");
            report.append(String.format("TOTAL FINE OWED: ₪%d\n", totalFine));
            report.append("=".repeat(50)).append("\n");
        }

        return report.toString();
    }

   /* public List<MediaCopy> getCopiesByISBN(String isbn) {
        List<MediaCopy> copies = new ArrayList<>();

        MediaItem item = findMediaByIsbn(isbn);
        if (item == null) return copies;

        try (BufferedReader br = new BufferedReader(new FileReader("media_copies.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 3) continue;

                String copyId = parts[0];
                String copyIsbn = parts[1];
                boolean available = Boolean.parseBoolean(parts[2]);

                if (copyIsbn.equals(isbn)) {
                    copies.add(new MediaCopy(copyId, item, available));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return copies;
    }*/
    /**
     * Retrieves a list of media copies for a given ISBN.
     * This method retrieves all copies of a media item (Book or CD)
     * from the repository based on the provided ISBN.
     *
     * @param isbn the ISBN of the media item
     * @return a list of media copies associated with the given ISBN
     */
    public List<MediaCopy> getCopiesByISBN(String isbn) {
        return FileMediaCopyRepository.getInstance().getCopiesByIsbn(isbn);
    }

}