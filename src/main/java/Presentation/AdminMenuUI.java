package Presentation;

import Service.BookService;
import Service.AdminService;
import Domain.Book;
import Service.BookServiceAdmin;

import java.util.Scanner;

public class AdminMenuUI {

    private Scanner cin = new Scanner(System.in);
    private BookServiceAdmin bookService = new BookServiceAdmin();

    public void show(AdminService adminService)
    {

        if(!adminService.isLoggedIn())
        {
            System.out.println("Access denied.");
            return;
        }


        while (true)
        {
            System.out.println("\n====== Admin Menu ======");
            System.out.println("1. Add Book");
            System.out.println("2. Search Book");
            System.out.println("3. Logout");
            System.out.println("=========================");

            System.out.print("Choose: ");
            int choice = getValidIntegerInput();

            if(choice == 1)
            {
                new AddBookUI().show(bookService);
            }

            else if (choice == 2)
            {
                new SearchBookUI().show(bookService);
            }

            else if (choice == 3)
            {
                adminService.logout();
                System.out.println("Logged out!");
                break;
            }

        }

    }
    private int getValidIntegerInput() {
        while (true) {
            try {
                return Integer.parseInt(cin.nextLine()); // Try to parse the input as an integer
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }


}
