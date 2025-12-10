package Presentation;

import java.util.Scanner;

import Service.BookService;
import Domain.Book;
import Service.BookServiceAdmin;
/**
 * User interface for adding a new book to the system.
 */
public class AddBookUI {
    public static final String SEPARATOR = "~~~~~~~~~~";
    private Scanner cin = new Scanner(System.in);
    /**
     * Displays the UI for adding a book and handles user input.
     *
     * @param bookService the BookServiceAdmin instance to add the book
     */
    public void show(BookServiceAdmin bookService)
    {
        System.out.println("\n====== Add Book ======");

        System.out.print("Book Title: ");
        String title = cin.nextLine();

        System.out.println(SEPARATOR);

        System.out.print("Author: ");
        String author = cin.nextLine();

        System.out.println(SEPARATOR);

        System.out.print("ISBN: ");
        String isbn = cin.nextLine();

        System.out.println(SEPARATOR);

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
