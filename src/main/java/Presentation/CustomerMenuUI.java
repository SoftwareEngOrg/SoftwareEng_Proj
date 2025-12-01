package Presentation;

import Domain.User;
import Service.BookServiceCustomer;
import Service.Doenev;
import Service.InputValidator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class CustomerMenuUI {
    private final BookServiceCustomer bookService = new BookServiceCustomer();
    Doenev di = new Doenev();

    private final Scanner scanner = new Scanner(System.in);

    public void show(User loggedInUser) {
        bookService.setCurrentUser(loggedInUser);
        bookService.setEmailConfig(di.getUsername() , di.getPassword());
        while (true) {
            System.out.println("\n====== Customer Menu ======");
            System.out.println("1. Browse Available Books");
            System.out.println("2. Browse Available CDs");
            System.out.println("3. Search Book");
            System.out.println("4. Borrow Book");
            System.out.println("5. Borrow CD");
            System.out.println("6. return Item/pay fine");
            System.out.println("7. View My Loans & Fines");
            System.out.println("8. overdue report (Save to File)");
            System.out.println("9. Logout");
            System.out.println("===========================");

            System.out.print("Choose: ");
            int choice = InputValidator.getValidIntegerInput();

            switch (choice) {
                case 1 -> browseBooks();
                case 2 -> browseCDs();
                case 3 -> new SearchBookUI().show(bookService);
                case 4,5 -> borrowBook();
                case 6 -> returnItem();
                case 7 -> bookService.viewMyLoans();
                case 8 -> printReport(loggedInUser);
                case 9 -> {
                    System.out.println("Logged out successfully!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void printReport(User loggedInUser) {
        if (loggedInUser == null) {
            System.out.println("Error: Not logged in.");
            return;
        }
        String filename = loggedInUser.getUsername() + ".txt";
        String reportContent = bookService.generateLoanReport();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.print(reportContent);
            System.out.println("\n✅ Report successfully saved to: " + filename);
            System.out.println("\nReport Preview:");
            System.out.println(reportContent);
        } catch (IOException e) {
            System.out.println("❌ Error saving report: " + e.getMessage());
        }
    }

    private void browseCDs() {
        var CDs = bookService.getAllAvailableCDs();
        if (CDs.isEmpty()) {
            System.out.println("No CDs available right now.");
            return;
        }
        System.out.println("\n--- Available CDs ---");
        CDs.forEach(b -> System.out.println(b + " [Available]"));
        System.out.println("-----------------------");
    }

    private void browseBooks() {
        var books = bookService.getAllAvailableBooks();
        if (books.isEmpty()) {
            System.out.println("No books available right now.");
            return;
        }
        System.out.println("\n--- Available Books ---");
        books.forEach(b -> System.out.println(b + " [Available]"));
        System.out.println("-----------------------");
    }

    private void borrowBook() {
        System.out.print("Enter ISBN of the book to borrow: ");
        String isbn = scanner.nextLine().trim();
        bookService.borrowMediaItem(isbn);
    }

    private void returnItem() {
        System.out.print("Enter your Loan ID (shown when you borrowed): ");
        String loanId = scanner.nextLine().trim();

        boolean returnSuccess = bookService.returnBook(loanId);

        if (!returnSuccess) {
            System.out.println("\nDo you want to pay this fine?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.print("Choose: ");

            int choice = InputValidator.getValidIntegerInput();

            if (choice == 1) {
                bookService.completeReturn(loanId);
            } else {
                System.out.println("Return cancelled. Please pay your fine to return the book.");
            }
        }
    }

}
