package Presentation;

import java.util.List;
import java.util.Scanner;

import Service.BookService;
import Domain.Book;
import Service.InputValidator;

/**
 * User interface class for searching books in the library system.
 * <p>
 * Provides options to search by title, author, or ISBN, and displays
 * the search results in the console.
 * </p>
 *
 * @since 1.0
 */
public class SearchBookUI {

    /** Scanner used to read user input from the console. */
    private Scanner cin = new Scanner(System.in);

    /**
     * Displays the search menu and handles user input to search for books.
     * Uses the provided {@link BookService} to perform searches.
     *
     * @param bookService the service used to search for books
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
        if (choice == 4)
            return;

        System.out.print("Enter value: ");
        String value = cin.nextLine();

        List<Book> result = null;

        if (choice == 1) {
            result = bookService.searchByTitle(value);
        } else if (choice == 2) {
            result = bookService.searchByAuthor(value);
        } else if (choice == 3) {
            result = bookService.searchByISBN(value);
        } else {
            return;
        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Results:");
        for (Book b : result) {
            System.out.println(b);
        }
        System.out.println("========================");
    }
}
