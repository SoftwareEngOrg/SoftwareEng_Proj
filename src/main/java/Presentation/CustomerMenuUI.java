package Presentation;

import Domain.Book;
import Domain.CD;
import Domain.MediaCopy;
import Domain.User;
import Service.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

/**
 * User interface for the customer menu in the library system.
 * <p>
 * Allows a logged-in customer to browse books and CDs, borrow items,
 * return items/pay fines, view loans, generate overdue reports, and log out.
 * </p>
 *
 * @since 1.0
 */
public class CustomerMenuUI {

    /** Service used for customer operations related to books and CDs. */
    private final BookServiceCustomer bookService = new BookServiceCustomer();

    /** Email helper used for notifications or reports. */
    Doenev di = new Doenev();

    /** Scanner used to read input from the console. */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Displays the customer menu for a logged-in user and handles input choices.
     *
     * @param loggedInUser the currently logged-in user
     */
    public void show(User loggedInUser) {
        bookService.setCurrentUser(loggedInUser);
        bookService.setEmailConfig(di.getUsername(), di.getPassword());

        System.out.println("\n====== Welcome " + loggedInUser.getUsername() + " ======\n");

        while (true) {
            System.out.println("====== Customer Menu ======");
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

    /**
     * Generates and saves the overdue report for the logged-in user to a text file.
     * <p>
     * Also displays the report content in the console.
     * </p>
     *
     * @param loggedInUser the currently logged-in user
     */
    private void printReport(User loggedInUser) {
        if (loggedInUser == null) {
            System.out.println("Error: Not logged in.");
            return;
        }

        String filename = loggedInUser.getUsername() + ".txt";
        String reportContent = bookService.generateLoanReport();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.print(reportContent);
            System.out.println("\n Report successfully saved to: " + filename);
            System.out.println("\nReport Preview:");
            System.out.println(reportContent);
        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }

    /**
     * Displays all available CDs in the library along with their available copies.
     * Updates availability if no copies remain.
     */
    private void browseCDs() {
        var cds = bookService.getAllAvailableCDs();
        if (cds.isEmpty()) {
            System.out.println("No CDs available right now.");
            return;
        }

        System.out.println("\n--- Available CDs ---");

        for (CD cd : cds) {
            List<MediaCopy> copies = bookService.getCopiesByISBN(cd.getIsbn());
            int availableCount = (int) copies.stream().filter(MediaCopy::isAvailable).count();

            if (availableCount == 0) {
                cd.setAvailable(false);
            }

            System.out.println(cd.getTitle() + " | Artist: " + cd.getAuthor()
                    + " | Isbn: " + cd.getIsbn()
                    + " | Available Copies: " + availableCount);
        }

        System.out.println("-----------------------");
    }

    /**
     * Displays all available books in the library along with their available copies.
     * Updates availability if no copies remain.
     */
    private void browseBooks() {
        var books = bookService.getAllAvailableBooks();
        if (books.isEmpty()) {
            System.out.println("No books available right now.");
            return;
        }

        System.out.println("\n--- Available Books ---");

        for (Book book : books) {
            List<MediaCopy> copies = bookService.getCopiesByISBN(book.getIsbn());
            int availableCount = (int) copies.stream().filter(MediaCopy::isAvailable).count();

            if (availableCount == 0) {
                book.setAvailable(false);
            }

            System.out.println(book.getTitle() + " | Author: " + book.getAuthor()
                    + " | Isbn: " + book.getIsbn()
                    + " | Available Copies: " + availableCount);
        }

        System.out.println("-----------------------");
    }

    /**
     * Prompts the user to borrow a book or CD by entering its ISBN/ID.
     */
    private void borrowBook() {
        System.out.print("Enter ISBN of the book to borrow: ");
        String isbn = scanner.nextLine().trim();
        bookService.borrowMediaItem(isbn);
    }

    /**
     * Handles the return of a borrowed item.
     * If the item is overdue, prompts the user to pay any fines.
     */
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
