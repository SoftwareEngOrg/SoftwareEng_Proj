package Presentation;

import Domain.User;
import Service.BookServiceCustomer;
import Service.InputValidator;

import java.util.Scanner;


public class CustomerMenuUI {
    private final BookServiceCustomer bookService = new BookServiceCustomer();
    private final Scanner scanner = new Scanner(System.in);

    public void show(User loggedInUser) {
        bookService.setCurrentUser(loggedInUser);
        while (true) {
            System.out.println("\n====== Customer Menu ======");
            System.out.println("1. Browse Available Books");
            System.out.println("2. Search Book");
            System.out.println("3. Borrow Book");
            System.out.println("4. return book/pay fine");
            System.out.println("5. View My Loans & Fines");
            System.out.println("6. Logout");
            System.out.println("===========================");

            System.out.print("Choose: ");
            int choice = InputValidator.getValidIntegerInput();

            switch (choice) {
                case 1 -> browseBooks();
                case 2 -> new SearchBookUI().show(bookService);
                case 3 -> borrowBook();
                case 4 -> returnBook();
                case 5 -> bookService.viewMyLoans();
                case 6 -> {
                    System.out.println("Logged out successfully!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
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
        bookService.borrowBook(isbn);
    }

    private void returnBook() {
        System.out.print("Enter your Loan ID (shown when you borrowed): ");
        String loanId = scanner.nextLine().trim();

        boolean returnSuccess = bookService.returnBook(loanId);

        if (!returnSuccess) {
            // Check if there's a fine to pay
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
