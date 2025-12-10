package Presentation;

import Domain.User;
import Service.AdminService;
import Service.FileUserRepository;

import java.util.Scanner;

/**
 * UI for user login. Verifies credentials and redirects to appropriate menu based on user role.
 */
public class LoginUI {

    private final FileUserRepository userRepo = new FileUserRepository();
    private final Scanner cin = new Scanner(System.in);

    /**
     * Prompts for username and password, validates them, and redirects to the appropriate user interface.
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

                // Redirect based on user role
                if (foundUser.getRole().equals("admin")) {
                    AdminService adminService = new AdminService();
                    adminService.login(user, pass);
                    new AdminMenuUI().show(adminService);
                } else if (foundUser.getRole().equals("customer")) {
                    userRepo.updateDate(foundUser);
                    new CustomerMenuUI().show(foundUser);
                } else if (foundUser.getRole().equals("librarian")) {
                    System.out.println("Welcome, Librarian " + foundUser.getUsername() + "!");
                    userRepo.updateDate(foundUser);
                    new LibrarianMenuUI().show();
                }
                nonvalidUser = false;
            } else {
                System.out.println("Invalid credentials");
                System.out.println("======================");
            }
        }
    }
}
