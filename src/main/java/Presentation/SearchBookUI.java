package Presentation;

import java.util.List;
import java.util.Scanner;

import Service.BookService;
import Domain.Book;
import Service.InputValidator;

/**
 * UI for searching books by title, author, or ISBN.
 */
public class SearchBookUI {
    private final Scanner cin = new Scanner(System.in);

    /**
     * Displays the search options and handles the book search based on user input.
     *
     * @param bookService the service to handle book search operations
     */
    public void show(BookService bookService) {
        System.out.println("\n====== Search Book ======");
        System.out.println("Search by:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. ISBN");
        System.out.println("4. Go back");
        System.out.println("========================");

        System.out.print("Choose: ");
        int choice = InputValidator.getValidIntegerInput();
        if (choice == 4) return;

        System.out.print("Enter value: ");
        String value = cin.nextLine();

        List<Book> result = null;

        // Search based on the user's choice
        if (choice == 1) {
            result = bookService.searchByTitle(value);
        } else if (choice == 2) {
            result = bookService.searchByAuthor(value);
        } else if (choice == 3) {
            result = bookService.searchByISBN(value);
        } else {
            return; // Invalid choice, exit
        }

        // Display search results
        System.out.println("~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Results:");
        for (Book b : result) {
            System.out.println(b);
        }
        System.out.println("========================");
    }
}
