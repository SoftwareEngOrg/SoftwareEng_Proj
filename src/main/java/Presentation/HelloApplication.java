package Presentation;

import Service.BookServiceCustomer;
import Service.InputValidator;
import Service.Doenev;

/**
 * Main entry point for the library application.
 * <p>
 * Displays the initial console menu for users to login, sign up, or exit.
 * Initializes necessary services for customer operations and authentication.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre><code>
 * java Presentation.HelloApplication
 * </code></pre>
 *
 * @since 1.0
 */
public class HelloApplication {

    /**
     * Main method that starts the library application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        Doenev di = new Doenev();
        BookServiceCustomer service = new BookServiceCustomer(di.getUsername(), di.getPassword());

        while (true) {
            System.out.println("==================================================");
            System.out.println("======== Hello and welcome to our Library ========");
            System.out.println("1. Login");
            System.out.println("2. signup");
            System.out.println("3. exit");
            System.out.println("==================================================");
            System.out.print("Choose: ");
            int choice = InputValidator.getValidIntegerInput();

            if (choice == 1) {
                LoginUI login = new LoginUI();
                login.show();
            } else if (choice == 2) {
                SignUp sign = new SignUp();
                sign.show();
            } else if (choice == 3) {
                break;
            }
        }
    }
}
