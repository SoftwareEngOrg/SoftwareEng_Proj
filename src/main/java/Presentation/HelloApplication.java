package Presentation;

import Service.BookServiceCustomer;
import Service.InputValidator;
import Service.Doenev;

/**
 * Main entry point for the Library application. Provides login, signup, and exit options.
 */
public class HelloApplication {

    public static void main(String[] args) {

        Doenev di = new Doenev();
        BookServiceCustomer service = new BookServiceCustomer(di.getUsername(), di.getPassword());

        while (true) {
            System.out.println("==================================================");
            System.out.println("======== Hello and welcome to our Library ========");
            System.out.println("1. Login");
            System.out.println("2. Signup");
            System.out.println("3. Exit");
            System.out.println("==================================================");
            System.out.print("Choose: ");

            int choice = InputValidator.getValidIntegerInput();

            switch (choice) {
                case 1:
                    // Show login UI
                    LoginUI login = new LoginUI();
                    login.show();
                    break;
                case 2:
                    // Show signup UI
                    SignUp sign = new SignUp();
                    sign.show();
                    break;
                case 3:
                    // Exit the program
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
