package Presentation;

import Domain.User;
import Service.*;

import java.util.List;
import java.util.Scanner;

/**
 * Provides the administrator interface for managing the library system.
 *
 * <p>This UI allows administrators to:
 * <ul>
 *     <li>Add books and CDs</li>
 *     <li>Search for books</li>
 *     <li>View and unregister inactive users</li>
 *     <li>Send reminder emails to users with overdue loans</li>
 *     <li>Log out</li>
 * </ul>
 *
 * <p>The class works as a presentation-layer component and communicates
 * with several service-layer classes to perform operations.
 *
 * @since 1.0
 * @see BookServiceAdmin
 * @see AdminService
 * @see ReminderService
 */
public class AdminMenuUI {

    /** Scanner for reading user input from the console. */
    private Scanner cin = new Scanner(System.in);

    /** Service layer for managing books, CDs, and user administrative actions. */
    private BookServiceAdmin bookService = new BookServiceAdmin();

    /**
     * Displays the main administrator menu and executes the selected option.
     *
     * <p>The method first verifies that the administrator is logged in. If not,
     * access is denied. The menu runs in a loop until the admin chooses to log out.
     *
     * @param adminService the service responsible for authentication and admin-specific actions
     * @see AddBookUI
     * @see AddCDUI
     * @see SearchBookUI
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

            if (choice == 1) {
                new AddBookUI().show(bookService);
            } else if (choice == 2) {
                new AddCDUI().show(bookService);
            } else if (choice == 3) {
                new SearchBookUI().show(bookService);
            } else if (choice == 4) {
                viewInactiveUsersUI();
            } else if (choice == 5) {
                Reminder();
            } else if (choice == 6) {
                adminService.logout();
                System.out.println("Logged out!");
                break;
            }
        }
    }

    /**
     * Displays users who have overdue loans and optionally sends reminder emails.
     *
     * <p>If overdue users exist, the admin is prompted whether to send reminder emails.
     * This operation uses {@link ReminderService}.
     *
     * @see ReminderService#displayOverdueUsers()
     * @see ReminderService#sendReminders()
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
     * Displays a list of inactive users and provides administrative options:
     *
     * <ul>
     *     <li>Unregister all inactive users</li>
     *     <li>Unregister one user by username</li>
     *     <li>Return to the main admin menu</li>
     * </ul>
     *
     * @see BookServiceAdmin#viewInactiveUsers()
     * @see BookServiceAdmin#unregisterAllUsers(List)
     * @see BookServiceAdmin#unregisterUserByUsername(String)
     */
    private void viewInactiveUsersUI() {
        List<User> inActiveUsers = bookService.viewInactiveUsers();
        displayInactiveUsers(inActiveUsers);

        while (true) {
            int choice = getAdminMenuChoice();
            if (choice == 1) {
                unregisterAllUsers(inActiveUsers);
                break;
            } else if (choice == 2) {
                unregisterUserByUsername(inActiveUsers);
                break;
            } else if (choice == 3) {
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Displays the list of inactive users.
     *
     * @param inActiveUsers the list of inactive users to display
     */
    private void displayInactiveUsers(List<User> inActiveUsers) {
        if (inActiveUsers.isEmpty()) {
            System.out.println("No inactive users found.");
        } else {
            System.out.println("\n====== Inactive Users ======");
            for (User user : inActiveUsers) {
                System.out.println(user);
            }
            System.out.println("=============================");
        }
    }

    /**
     * Displays the submenu for inactive user handling and returns the admin's choice.
     *
     * @return the selected option as an integer
     */
    private int getAdminMenuChoice() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Unregister all users");
        System.out.println("2. Unregister a user by username");
        System.out.println("3. Go back");
        System.out.print("Choose: ");
        return InputValidator.getValidIntegerInput();
    }

    /**
     * Unregisters all inactive users using the service layer.
     *
     * @param inActiveUsers the list of inactive users to unregister
     */
    private void unregisterAllUsers(List<User> inActiveUsers) {
        bookService.unregisterAllUsers(inActiveUsers);
    }

    /**
     * Unregisters a specific user by username after verifying they are inactive.
     *
     * @param inActiveUsers the list of inactive users
     */
    private void unregisterUserByUsername(List<User> inActiveUsers) {
        System.out.print("Enter username to unregister: ");
        String username = cin.nextLine();
        boolean userFound = false;

        for (User user : inActiveUsers) {
            if (user.getUsername().equals(username)) {
                bookService.unregisterUserByUsername(username);
                userFound = true;
                break;
            }
        }

        if (!userFound) {
            System.out.println("This user is not inactive or doesn't exist.");
        }
    }
}
