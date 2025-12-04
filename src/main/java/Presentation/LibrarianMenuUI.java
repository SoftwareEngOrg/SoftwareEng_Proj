package Presentation;

import Domain.Loan;
import Service.FileLoanRepository;
import Service.InputValidator;
import Service.LibrarianService;

import java.time.LocalDate;
import java.util.List;

public class LibrarianMenuUI {
    private final LibrarianService librarianService = new LibrarianService();

    public void show() {
        while (true) {
            System.out.println("\n====== Librarian Menu ======");
            System.out.println("1. View All Overdue Loans");
            System.out.println("2. View All Active Loans");
            System.out.println("3. Search Overdue by Username");
            System.out.println("4. Logout");
            System.out.println("============================");

            System.out.print("Choose: ");
            int choice = InputValidator.getValidIntegerInput();
            LocalDate today = LocalDate.now();


            switch (choice) {
                case 1 -> viewAllOverdue(today);
                case 2 -> viewAllActiveLoans();
                case 3 -> searchOverdueByUser(today);
                case 4 -> {
                    System.out.println("Librarian logged out.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewAllOverdue(LocalDate today) {
        var overdue = librarianService.getOverdueLoans(today);

        if (overdue.isEmpty()) {
            System.out.println("No overdue loans. Excellent!");
            return;
        }

        System.out.println("\nOVERDUE LOANS (" + overdue.size() + "):");
        System.out.println("------------------------------------------------------------------------");
        int totalFine = 0;
        for (Loan loan : overdue) {
            int fine = loan.calculateFine(today);
            totalFine += fine;
            System.out.printf("• %-12s | %-30s | Due: %-12s | Days late: %-3d | Fine: ₪%-4d | ID: %s\n",
                    loan.getUser().getUsername(),
                    loan.getMediaItem().getTitle(),
                    loan.getDueDate(),
                    loan.getOverdueDays(today),
                    fine,
                    loan.getLoanId());
        }
        System.out.println("------------------------------------------------------------------------");
        System.out.println("TOTAL FINES TO COLLECT: ₪" + totalFine);
    }

    private void viewAllActiveLoans() {
        var loans = librarianService.getAllActiveLoans();
        if (loans.isEmpty()) {
            System.out.println("No active loans.");
            return;
        }
        System.out.println("\nALL ACTIVE LOANS:");
        for (Loan l : loans) {
            String status = l.isOverdue(LocalDate.now()) ? " [OVERDUE]" : "";
            System.out.printf("• %-12s → %-30s | Due: %s%s\n",
                    l.getUser().getUsername(), l.getMediaItem().getTitle(), l.getDueDate(), status);
        }
    }

    private void searchOverdueByUser(LocalDate today) {
        System.out.print("Enter username: ");
        String username = new java.util.Scanner(System.in).nextLine().trim();

        var overdue = librarianService.getOverdueLoansForUser(username, today);
        if (overdue.isEmpty()) {
            System.out.println("No overdue loans for: " + username);
        } else {
            System.out.println("\nOverdue loans for: " + username);
            overdue.forEach(loan -> {
                int fine = loan.calculateFine(today);
                System.out.printf("• %s | Due: %s | Fine: ₪%d | ID: %s\n",
                        loan.getMediaItem().getTitle(), loan.getDueDate(), fine, loan.getLoanId());
            });
        }
    }
}
