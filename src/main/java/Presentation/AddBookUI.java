package Presentation;

import java.util.Scanner;
import Service.BookServiceAdmin;
import Domain.Book;

/**
 * User interface for adding a new book to the system.
 * Prompts the user for book details (title, author, ISBN, number of copies)
 * and uses BookServiceAdmin to add the book.
 *
 * @since 1.0
 */
public class AddBookUI {

    private Scanner cin = new Scanner(System.in);

    /**
     * Displays the UI for adding a book. Prompts user for book details,
     * attempts to add the book (with the given number of copies) via the service,
     * and prints the result message.
     *
     * @param bookService the BookServiceAdmin instance used to add the book
     */
    public void show(BookServiceAdmin bookService) {
        System.out.println("\n====== Add Book ======");

        System.out.print("Book Title: ");
        String title = cin.nextLine();

        System.out.println("~~~~~~~~~~");

        System.out.print("Author: ");
        String author = cin.nextLine();

        System.out.println("~~~~~~~~~~");

        System.out.print("ISBN: ");
        String isbn = cin.nextLine();

        System.out.println("~~~~~~~~~~");

        System.out.print("Number of Copies: ");
        int numberOfCopies = Integer.parseInt(cin.nextLine());

        Book b = new Book(title, author, isbn);
        String result = bookService.addBook(b, numberOfCopies)
                ? "Book added with " + numberOfCopies + " copies!"
                : "A book or CD with this ISBN already exists!";
        System.out.println(result);

        System.out.println("========================");
    }

}
