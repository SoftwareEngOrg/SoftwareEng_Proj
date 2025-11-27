package Presentation;

import java.util.Scanner;

import Service.BookService;
import Domain.Book;

public class AddBookUI {

    private Scanner cin = new Scanner(System.in);

    public void show(BookService bookService)
    {
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

        Book b = new Book(title, author, isbn);
        bookService.addBook(b);

        System.out.println("Book added!");

        System.out.println("========================");

    }

}
