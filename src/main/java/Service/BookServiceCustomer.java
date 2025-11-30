package Service;

import Domain.Book;
import Domain.Loan;
import Domain.MediaItem;
import Domain.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookServiceCustomer extends BookService{

    private  FileLoanRepository loanRepository = new FileLoanRepository();
    private  FileBookRepository fileBook = FileBookRepository.getInstance();
    private User currentUser;
    private Map<String, List<User>> waitList = new HashMap<>();


    private String emailUser;
    private String emailPass;



    public void setCurrentUser(User user) {
        this.currentUser = user;
    }


    public BookServiceCustomer(String emailUser, String emailPass) {
        this.emailUser = emailUser;
        this.emailPass = emailPass;
    }

    public BookServiceCustomer() {}

    public void setEmailConfig(String emailUser, String emailPass) {
        this.emailUser = emailUser;
        this.emailPass = emailPass;
    }


    public boolean borrowBook(String isbn)
    {
        if (currentUser == null)
        {
            System.out.println("Error: User not logged in.");
            return false;
        }
        Book item = fileBook.findAllBooks().stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);

        if (item == null)
        {
            System.out.println("Book with ISBN '" + isbn + "' not found.");
            return false;
        }

        if (!item.isAvailable())
        {
            System.out.println("Book is borrowed.");
            System.out.println("We will notify you by email when it becomes available.");

            if (currentUser.getEmail() == null || currentUser.getEmail().isEmpty()) {
                System.out.println("Cannot add to waitlist: user email not set.");
                return false;
            }

            waitList.putIfAbsent(isbn, new ArrayList<>());
            waitList.get(isbn).add(currentUser);

            System.out.println("=== DEBUG: currentUser inside BookServiceCustomer ===");
            System.out.println("Username: " + currentUser.getUsername());
            System.out.println("Email: " + currentUser.getEmail());

            System.out.println("Emailrrrrrrrrrrrrr: " + emailUser);


            EmailService es = new EmailService(this.emailUser, this.emailPass);
            CustomerObserver observer = new CustomerObserver(currentUser, es);
            BookInventory.getInstance().addObserver(observer);

            return false;
        }

        // Check for overdue loans or unpaid fines
        LocalDate today = LocalDate.now();
        List<Loan> activeLoans = loanRepository.getActiveLoansForUser(currentUser.getUsername());

        boolean hasOverdue = activeLoans.stream().anyMatch(loan -> loan.isOverdue(today));
        int totalFine = activeLoans.stream().mapToInt(loan -> loan.calculateFine(today)).sum();

        if (hasOverdue || totalFine > 0) {
            System.out.println("Cannot borrow: You have overdue books or unpaid fines (₪" + totalFine + ").");
            return false;
        }
        Loan loan = loanRepository.borrowItem(currentUser, item);
        System.out.println("Book borrowed successfully!");
        System.out.println("Loan ID: " + loan.getLoanId());
        System.out.println("Due date: " + loan.getDueDate());
        return true;
    }



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
        } else {
            // No fine - process return immediately
            loanRepository.returnItem(loanId, today);
            System.out.println("Book returned on time. Thank you!");

            // notify all observers
            BookInventory.getInstance().notifyBookReturned(loan.getMediaItem().getIsbnOrId());
            return true;
        }
    }
    public boolean completeReturn(String loanId) {
        LocalDate today = LocalDate.now();
        Loan loan = loanRepository.findLoanById(loanId);

        if (loan == null || loan.getReturnDate() != null) {
            System.out.println("Invalid or already returned Loan ID.");
            return false;
        }

        loanRepository.returnItem(loanId, today);
        System.out.println("Fine paid. Book returned successfully!");

        // notify all observers
        BookInventory.getInstance().notifyBookReturned(loan.getMediaItem().getIsbnOrId());
        return true;
    }

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

        System.out.println("\n=== Your Active Loans ===");
        int totalFine = 0;
        for (Loan loan : myLoans) {
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
        if (totalFine > 0) {
            System.out.println("Total fine owed: ₪" + totalFine);
        }
    }

    public List<Book> getAllAvailableBooks() {
        return fileBook.findAllBooks().stream()
                .filter(MediaItem::isAvailable)
                .toList();
    }
}