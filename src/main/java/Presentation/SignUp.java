package Presentation;

import Domain.User;
import Service.FileUserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * User interface class for signing up new users in the library system.
 * <p>
 * Prompts the user to enter a username, email, and password. Validates
 * that the username is unique and registers the user with the role "customer".
 * Upon successful registration, redirects the user to the customer menu.
 * </p>
 *
 * @since 1.0
 */
public class SignUp {

    /** Repository used to manage and store user data. */
    private FileUserRepository repo = new FileUserRepository();

    /** Scanner used to read user input from the console. */
    private Scanner scanner = new Scanner(System.in);

    /**
     * Displays the sign-up form, collects user input, validates the username,
     * and registers the user. Redirects to {@link CustomerMenuUI} upon success.
     */
    public void show() {
        System.out.println("======== Sign Up ========");

        String username;
        while (true) {
            System.out.print("Enter your username: ");
            username = scanner.nextLine();

            if (!repo.isUsernameExists(username)) {
                break;
            } else {
                System.out.println("Username already exists! Please choose another one.");
            }
        }

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(new Date());

        Date currentDate = null;
        try {
            currentDate = dateFormat.parse(formattedDate);
            System.out.println(dateFormat.format(currentDate));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        boolean success = repo.addUser(username, password, email, currentDate);

        if (success) {
            System.out.println("Sign Up successful!");
            System.out.println("Your username is: " + username);
            System.out.println("Role: customer");

            User foundUser = new User(username, password, "customer", email, currentDate);
            CustomerMenuUI customerMenu = new CustomerMenuUI();
            customerMenu.show(foundUser);
        } else {
            System.out.println("Sign Up failed. Try again.");
        }
    }
}
