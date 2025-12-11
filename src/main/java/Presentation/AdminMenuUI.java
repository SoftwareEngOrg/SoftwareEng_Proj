package Presentation;

import Domain.User;
import Service.*;
import Domain.Book;

import java.util.List;
import java.util.Scanner;

/**
 * User interface class that provides the administrator menu and related actions.
 * <p>
 * This menu allows an admin to add books or CDs, search for books,
 * manage inactive users, send reminders, and log out.
 * It communicates with multiple service-layer classes to perform the operations.
 * </p>
 */
public class AdminMenuUI {

    /** Scanner for reading user input from the console. */
    private Scanner cin = new Scanner(System.in);

    /** Service used for book and CD operations. */
    private BookServiceAdmin bookService = new BookServiceAdmin();

    /**
     * Displays the administrator menu and handles user selections.
     * Ensures that the user is logged in before allowing access.
     *
     * @param adminService the service responsible for authentication and admin actions
     */
    public void show(AdminService adminService)
    {
        if (!adminService.isLoggedIn())
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
            System.out.println("5. Reminder");
            System.out.println("6. Logout");
            System.out.println("=========================");

            System.out.print("Choose: ");
            int choice = InputValidator.getValidIntegerInput();

            if (choice == 1)
            {
                new AddBookUI().show(bookService);
            }
            else if (choice == 2)
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
                Reminder();
            }
            else if (choice == 6)
            {
                adminService.logout();
                System.out.println("Logged out!");
                break;
            }
        }
    }

    /**
     * Displays users who have overdue loans and optionally sends reminder emails.
     * <p>
     * This method uses {@link ReminderService} to check overdue users and send notifications.
     * </p>
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
     * Displays a list of inactive users and allows the admin to:
     * <ul>
     *   <li>Unregister all inactive users</li>
     *   <li>Unregister a specific inactive user by username</li>
     *   <li>Return to the menu</li>
     * </ul>
     * <p>
     * Uses {@link BookServiceAdmin} to manage user removal.
     * </p>
     */
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
                for (User user : inActiveUsers)
                {
                    if (user.getUsername().equals(username))
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
