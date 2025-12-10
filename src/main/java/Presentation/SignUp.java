package Presentation;

import Domain.User;
import Service.FileUserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * UI for signing up a new user (customer).
 */
public class SignUp {

    private final FileUserRepository repo = new FileUserRepository();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Prompts the user for sign-up details and creates a new customer account.
     */
    public void show() {
        System.out.println("======== Sign Up ========");

        // Prompt for unique username
        String username;
        while (true) {
            System.out.print("Enter your username: ");
            username = scanner.nextLine();

            if (!repo.isUsernameExists(username)) {
                break; // Username is available
            } else {
                System.out.println("Username already exists! Please choose another one.");
            }
        }

        // Get user email and password
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // Set current date for account creation
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = null;
        try {
            currentDate = dateFormat.parse(dateFormat.format(new Date()));
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date", e);
        }

        // Add new user to repository
        boolean success = repo.addUser(username, password, email, currentDate);
        if (success) {
            System.out.println("Sign Up successful!");
            System.out.println("Your username is: " + username);
            System.out.println("Role: customer");

            // Redirect to customer menu
            User foundUser = new User(username, password, "customer", email, currentDate);
            new CustomerMenuUI().show(foundUser);
        } else {
            System.out.println("Sign Up failed. Try again.");
        }
    }
}
