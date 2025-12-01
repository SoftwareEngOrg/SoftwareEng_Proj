package Service;

import Domain.*;

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
    private FileCDRepository fileCD = FileCDRepository.getInstance();


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






    public boolean borrowMediaItem(String isbn) {
        if (currentUser == null) {
            System.out.println("Error: User not logged in.");
            return false;
        }

        MediaItem item = findMediaByIsbn(isbn);

        if (item == null) {
            System.out.println("Item with ISBN '" + isbn + "' not found.");
            return false;
        }

        if (!item.isAvailable()) {
            System.out.println(item.getClass().getSimpleName() + " is currently borrowed.");
            System.out.println("We will notify you by email when it becomes available.");

            if (currentUser.getEmail() == null || currentUser.getEmail().isEmpty()) {
                System.out.println("Cannot add to waitlist: user email not set.");
                return false;
            }

            waitList.putIfAbsent(isbn, new ArrayList<>());
            waitList.get(isbn).add(currentUser);

            EmailService es = new EmailService(this.emailUser, this.emailPass);
            CustomerObserver observer = new CustomerObserver(currentUser, es);
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

        Loan loan = loanRepository.borrowItem(currentUser, item);
        System.out.println(item.getClass().getSimpleName() + " borrowed successfully!");
        System.out.println("Loan ID: " + loan.getLoanId());
        System.out.println("Due date: " + loan.getDueDate());
        System.out.println("Borrowing period: " + item.getBorrowingPeriodDays() + " days");
        System.out.println("Fine per day if overdue: ₪" + item.getFinePerDay());
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

        // Separate loans into Books and CDs
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

        // Display Book Loans
        if (!bookLoans.isEmpty()) {
            System.out.println("\n📚 BOOK LOANS:");
            System.out.println("--------------------------------------");
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
            System.out.println("--------------------------------------");
        }

        // Display CD Loans
        if (!cdLoans.isEmpty()) {
            System.out.println("\n💿 CD LOANS:");
            System.out.println("--------------------------------------");
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
            System.out.println("--------------------------------------");
        }

        if (totalFine > 0) {
            System.out.println("\n💰 Total fine owed: ₪" + totalFine);
        }
    }


    public List<Book> getAllAvailableBooks() {
        return fileBook.findAllBooks().stream()
                .filter(MediaItem::isAvailable)
                .toList();
    }

    public List<CD> getAllAvailableCDs() {
        List<CD> all = new ArrayList<>();
        all.addAll(fileCD.findAllCDs().stream()
                .filter(MediaItem::isAvailable)
                .toList());
        return all;
    }

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

        // Separate loans into Books and CDs
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

        // Display CD Loans
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

}