package Presentation;
import java.util.List;
import java.util.Scanner;

import Service.BookService;
import Domain.Book;

public class SearchBookUI
{
    Scanner cin = new Scanner(System.in);

    public void show(BookService bookService)
    {
        System.out.println("\n====== Search Book ======");

        System.out.println("Search by:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. ISBN");
        System.out.println("========================");

        System.out.print("Choose: ");
        int choice = Integer.parseInt(cin.nextLine());

        System.out.print("Enter value: ");
        String value = cin.nextLine();

        List<Book> result = null;

        if (choice == 1)
        {
            result = bookService.searchByTitle(value);
        }

        if (choice == 2)
        {
            result = bookService.searchByAuthor(value);
        }

        if (choice == 3)
        {
            result = bookService.searchByISBN(value);
        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Results:");
        for (Book b : result) {
            System.out.println(b);
        }
        System.out.println("========================");

    }

}
