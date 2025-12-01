package Presentation;

import Domain.User;
import Service.BookService;
import Service.AdminService;
import Domain.Book;
import Service.BookServiceAdmin;
import Service.InputValidator;

import java.util.List;
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
            System.out.println("2. Add CD");
            System.out.println("3. Search Book");
            System.out.println("4. inactive users");
            System.out.println("5. Logout");
            System.out.println("=========================");

            System.out.print("Choose: ");
            int choice = InputValidator.getValidIntegerInput();

            if(choice == 1)
            {
                new AddBookUI().show(bookService);
            }
            else if(choice == 2)
            {
                new AddCDUI().show(bookService);
            }
            else if (choice == 3)
            {
                new SearchBookUI().show(bookService);
            }
            else if (choice == 4)
            {
                viewInactiveUsersUI();

            }
            else if (choice == 5)
            {
                adminService.logout();
                System.out.println("Logged out!");
                break;
            }
        }

    }

    private void viewInactiveUsersUI() {
        List<User> inActiveUsers = bookService.viewInactiveUsers();
        if (inActiveUsers.isEmpty()) {
            System.out.println("No inactive users found.");
        } else {
            System.out.println("\n====== Inactive Users ======");
            for (User user : inActiveUsers) {
                System.out.println(user);
            }
            System.out.println("=============================");
        }

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Unregister all users");
            System.out.println("2. Unregister a user by username");
            System.out.println("3. Go back");

            System.out.print("Choose: ");
            int choice = InputValidator.getValidIntegerInput();
            if (choice == 1) {
                bookService.unregisterAllUsers(inActiveUsers);
                break;
            }
            else if (choice == 2) {
                System.out.print("Enter username to unregister: ");
                String username = cin.nextLine();
                for(User user:inActiveUsers)
                {
                    if(user.getUsername().equals(username))
                    {
                        bookService.unregisterUserByUsername(username);
                        break;
                    }
                }
                System.out.print("this user you are trying to unregister is ACTIVE!!");
                break;
            }
            else if (choice == 3) {
                break;
            }
            else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }




}
