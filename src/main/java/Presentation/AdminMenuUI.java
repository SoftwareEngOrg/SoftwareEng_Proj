package Presentation;

import Domain.User;
import Service.*;

import java.util.List;
import java.util.Scanner;

/**
 * User interface for the admin menu. Provides options for managing books, users, and reminders.
 */
public class AdminMenuUI {

    private Scanner cin = new Scanner(System.in);
    private BookServiceAdmin bookService = new BookServiceAdmin();

    /**
     * Displays the admin menu and handles user input.
     *
     * @param adminService the AdminService instance to manage admin login/logout
     */
    public void show(AdminService adminService) {
        if (!adminService.isLoggedIn()) {
            System.out.println("Access denied.");
            return;
        }

        while (true) {
            System.out.println("\n====== Admin Menu ======");
            System.out.println("1. Add Book");
            System.out.println("2. Add CD");
            System.out.println("3. Search Book");
            System.out.println("4. Inactive Users");
            System.out.println("5. Reminder");
            System.out.println("6. Logout");
            System.out.println("=========================");

            System.out.print("Choose: ");
            int choice = InputValidator.getValidIntegerInput(); 

            switch (choice) {
                case 1:
                    new AddBookUI().show(bookService);
                    break;
                case 2:
                    new AddCDUI().show(bookService);
                    break;
                case 3:
                    new SearchBookUI().show(bookService);
                    break;
                case 4:
                    viewInactiveUsersUI();
                    break;
                case 5:
                    Reminder();
                    break;
                case 6:
                    adminService.logout();
                    System.out.println("Logged out!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Displays overdue loan reminder options and sends email reminders if requested.
     */
    private void Reminder() {
        System.out.println("\n====== Reminder: Users with Overdue Loans ======");

        ReminderService reminderService = new ReminderService();
        int overdueCount = reminderService.displayOverdueUsers();

        if (overdueCount == 0) {
            System.out.println("=".repeat(50));
            return;
        }
        System.out.println("=".repeat(50));

        System.out.print("\nDo you want to send email reminders to all users? (yes/no): ");
        String choice = cin.nextLine().trim().toLowerCase();

        if (choice.equals("yes") || choice.equals("y")) {
            reminderService.sendReminders();
        } else {
            System.out.println("Reminder cancelled.");
        }
    }

    /**
     * Displays and handles the management of inactive users.
     */
    private void viewInactiveUsersUI() {
        List<User> inActiveUsers = bookService.viewInactiveUsers();
        if (inActiveUsers.isEmpty()) {
            System.out.println("No inactive users found.");
        } else {
            System.out.println("\n====== Inactive Users ======");
            inActiveUsers.forEach(System.out::println);
            System.out.println("=============================");
        }

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Unregister all users");
            System.out.println("2. Unregister a user by username");
            System.out.println("3. Go back");

            System.out.print("Choose: ");
            int choice = InputValidator.getValidIntegerInput();
            switch (choice) {
                case 1:
                    bookService.unregisterAllUsers(inActiveUsers);
                    return;
                case 2:
                    System.out.print("Enter username to unregister: ");
                    String username = cin.nextLine();
                    inActiveUsers.stream()
                            .filter(user -> user.getUsername().equals(username))
                            .findFirst()
                            .ifPresent(user -> bookService.unregisterUserByUsername(username));
                    System.out.println("This user you are trying to unregister is ACTIVE!!");
                    return;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
