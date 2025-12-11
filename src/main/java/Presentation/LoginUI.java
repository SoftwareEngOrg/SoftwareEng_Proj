package Presentation;

import Domain.User;
import Service.AdminService;
import Service.FileUserRepository;

import java.util.Scanner;

/**
 * User interface class responsible for handling user login.
 * <p>
 * Prompts the user for username and password, validates credentials,
 * and redirects the user to the appropriate menu based on their role
 * (admin, customer, or librarian).
 * </p>
 *
 * @since 1.0
 */
public class LoginUI {

    /** Repository used to manage and validate users. */
    private FileUserRepository userRepo = new FileUserRepository();

    /** Scanner used to read input from the console. */
    private Scanner cin = new Scanner(System.in);

    /**
     * Displays the login form, prompts for credentials, and navigates
     * the user to the corresponding menu after successful authentication.
     * Repeats until valid credentials are provided.
     */
    public void show() {
        boolean nonvalidUser = true;

        while (nonvalidUser) {
            System.out.println();
            System.out.println("======== Hello and welcome to our Library ========");
            System.out.print("Username: ");
            String user = cin.nextLine();
            System.out.print("Password: ");
            String pass = cin.nextLine();

            User foundUser = userRepo.findUser(user, pass);

            if (foundUser != null) {
                System.out.println(foundUser.getEmail());
                System.out.println("Login successful!");

                switch (foundUser.getRole()) {
                    case "admin" -> {
                        AdminService adminService = new AdminService();
                        adminService.login(user, pass);
                        new AdminMenuUI().show(adminService);
                    }
                    case "customer" -> {
                        userRepo.updateDate(foundUser);
                        CustomerMenuUI customerMenu = new CustomerMenuUI();
                        customerMenu.show(foundUser);
                    }
                    case "librarian" -> {
                        System.out.println("Welcome, Librarian " + foundUser.getUsername() + "!");
                        userRepo.updateDate(foundUser);
                        new LibrarianMenuUI().show();
                    }
                }

                nonvalidUser = false;
            } else {
                System.out.println("Invalid credentials");
                System.out.println("======================");
                System.out.println();
            }
        }
    }
}
