package Service;

import Domain.Loan;
import Domain.User;

import java.time.LocalDate;
import java.util.*;

/**
 * The ReminderService class is responsible for managing overdue loan notifications.
 * It checks overdue loans, calculates fines, and sends email reminders to users with overdue items.
 */
public class ReminderService {

    private FileLoanRepository loanRepository;
    private FileUserRepository userRepository;
    private EmailService emailService;

    private LocalDate today;
    private List<Loan> overdueLoans;
    private Map<String, List<Loan>> userOverdueLoans;

    /**
     * Default constructor for the ReminderService class. Initializes the loan repository, user repository,
     * and email service, and loads the overdue loan data.
     */
    public ReminderService() {
        this.loanRepository = new FileLoanRepository();
        this.userRepository = new FileUserRepository();

        // Initialize environment (presumably to load credentials for email service)
        Doenev env = new Doenev();
        this.emailService = new EmailService(env.getUsername(), env.getPassword());

        initializeOverdueData();
    }

    /**
     * Initializes the overdue loan data by checking the current date and categorizing overdue loans by user.
     */
    private void initializeOverdueData() {
        this.today = LocalDate.now();
        this.overdueLoans = loanRepository.getOverdueLoans(today);
        this.userOverdueLoans = new HashMap<>();

        // Group overdue loans by user
        for (Loan loan : overdueLoans) {
            String username = loan.getUser().getUsername();
            userOverdueLoans.putIfAbsent(username, new ArrayList<>());
            userOverdueLoans.get(username).add(loan);
        }
    }

    /**
     * Displays the users with overdue items and their respective fines.
     *
     * @return the number of users with overdue items
     */
    public int displayOverdueUsers() {
        if (overdueLoans.isEmpty()) {
            System.out.println("No users with overdue loans.");
            return 0;
        }

        System.out.println("\nUsers with overdue items:");
        System.out.println("-".repeat(50));

        // Display each user with overdue items and their total fine
        for (Map.Entry<String, List<Loan>> entry : userOverdueLoans.entrySet()) {
            String username = entry.getKey();
            List<Loan> userLoans = entry.getValue();

            int totalFine = userLoans.stream()
                    .mapToInt(loan -> loan.calculateFine(today))
                    .sum();

            System.out.printf("• %s - %d overdue item(s) - Total fine: ₪%d\n",
                    username, userLoans.size(), totalFine);
        }

        System.out.println("-".repeat(50));
        System.out.println("Total users with overdue items: " + userOverdueLoans.size());

        return userOverdueLoans.size();
    }

    /**
     * Sends email reminders to users with overdue items.
     * The reminder includes a list of overdue items and the total fine due.
     */
    public void sendReminders() {
        if (overdueLoans.isEmpty()) {
            System.out.println("No overdue loans found. No reminders sent.");
            return;
        }

        System.out.println("\nSending reminders to " + userOverdueLoans.size() + " user(s)...\n");

        int successCount = 0;
        int failCount = 0;

        // Send reminder emails to each user
        for (Map.Entry<String, List<Loan>> entry : userOverdueLoans.entrySet()) {
            String username = entry.getKey();
            List<Loan> userLoans = entry.getValue();

            User user = userRepository.findUserByUsername(username);

            if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
                System.out.println("User '" + username + "' has no email. Skipping");
                failCount++;
                continue;
            }

            int totalFine = userLoans.stream()
                    .mapToInt(loan -> loan.calculateFine(today))
                    .sum();

            String subject = "Overdue Library Items Reminder";
            String body = buildReminderEmail(user, userLoans, totalFine);

            // Send email asynchronously
            emailService.sendEmailAsync(user.getEmail(), subject, body);

            System.out.println("Reminder sent to: " + username + " (" + user.getEmail() + ")");
            successCount++;
        }

        // Output success and failure counts
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Successfully sent: " + successCount);
        if (failCount > 0) {
            System.out.println("Failed/Skipped: " + failCount);
        }
        System.out.println("=".repeat(50));
    }

    /**
     * Builds the reminder email content for a user with overdue loans.
     *
     * @param user         the user to whom the reminder is sent
     * @param overdueLoans the list of overdue loans
     * @param totalFine    the total fine due for the overdue items
     * @return the formatted email body
     */
    private String buildReminderEmail(User user, List<Loan> overdueLoans, int totalFine) {
        StringBuilder email = new StringBuilder();

        email.append("Dear ").append(user.getUsername()).append(",\n\n");
        email.append("This is a friendly reminder that you have overdue items from our library.\n\n");
        email.append("=".repeat(50)).append("\n");
        email.append("OVERDUE ITEMS:\n");
        email.append("=".repeat(50)).append("\n\n");

        // List each overdue item and the fine
        for (Loan loan : overdueLoans) {
            int overdueDays = loan.getOverdueDays(today);
            int fine = loan.calculateFine(today);

            email.append("• Title: ").append(loan.getMediaItem().getTitle()).append("\n");
            email.append("  Author: ").append(loan.getMediaItem().getAuthor()).append("\n");
            email.append("  Due Date: ").append(loan.getDueDate()).append("\n");
            email.append("  Days Overdue: ").append(overdueDays).append("\n");
            email.append("  Fine: ₪").append(fine).append("\n\n");
        }

        email.append("=".repeat(50)).append("\n");
        email.append("TOTAL FINE: ₪").append(totalFine).append("\n");
        email.append("=".repeat(50)).append("\n\n");

        email.append("Please return the items as soon as possible to avoid additional fines.\n");
        email.append("Fine rate: ₪10 per day per item.\n\n");
        email.append("Thank you for your cooperation.\n\n");
        email.append("Best regards,\n");
        email.append("Library Management System");

        return email.toString();
    }

    /**
     * Gets the total number of users with overdue items.
     *
     * @return the number of users with overdue items
     */
    public int getOverdueUsersCount() {
        return userOverdueLoans.size();
    }

    /**
     * Gets a set of usernames of all users with overdue items.
     *
     * @return a set of usernames of users with overdue items
     */
    public Set<String> getOverdueUsernames() {
        return userOverdueLoans.keySet();
    }
}
