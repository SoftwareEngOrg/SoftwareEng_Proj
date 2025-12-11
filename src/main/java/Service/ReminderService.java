package Service;

import Domain.Loan;
import Domain.User;

import java.time.LocalDate;
import java.util.*;
/**
 * ReminderService handles detection of overdue loans and sends reminder emails
 * to users who have overdue items. It loads loan and user data from repositories,
 * groups overdue loans per user, calculates fines, displays summaries,
 * and sends reminder messages using the EmailService.
 *
 * Responsibilities:
 * - Load overdue loans
 * - Group loans by user
 * - Calculate overdue fines
 * - Display overdue users report
 * - Send reminder emails
 *
 * This class initializes all required data on construction.
 */
public class ReminderService {

    private FileLoanRepository loanRepository;
    private FileUserRepository userRepository;
    private EmailService emailService;

    private LocalDate today;
    private List<Loan> overdueLoans;
    private Map<String, List<Loan>> userOverdueLoans;


    public ReminderService() {
        this.loanRepository = new FileLoanRepository();
        this.userRepository = new FileUserRepository();

        Doenev env = new Doenev();
        this.emailService = new EmailService(env.getUsername(), env.getPassword());

        initializeOverdueData();
    }

    private void initializeOverdueData() {

        this.today = LocalDate.now();
        this.overdueLoans = loanRepository.getOverdueLoans(today);
        this.userOverdueLoans = new HashMap<>();

        for (Loan loan : overdueLoans) {
            String username = loan.getUser().getUsername();
            userOverdueLoans.putIfAbsent(username, new ArrayList<>());
            userOverdueLoans.get(username).add(loan);
        }

    }

    public int displayOverdueUsers() {

        if (overdueLoans.isEmpty()) {
            System.out.println("No users with overdue loans.");
            return 0;
        }

        System.out.println("\nUsers with overdue items:");
        System.out.println("-".repeat(50));

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

    public void sendReminders()
    {
        if (overdueLoans.isEmpty()) {
            System.out.println("No overdue loans found. No reminders sent.");
            return;
        }

        System.out.println("\nSending reminders to " + userOverdueLoans.size() + " user(s)...\n");

        int successCount = 0;
        int failCount = 0;

        for(Map.Entry<String , List<Loan>> entry : userOverdueLoans.entrySet() )
        {
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

            emailService.sendEmailAsync(user.getEmail(), subject, body);

            System.out.println("Reminder sent to: " + username + " (" + user.getEmail() + ")");
            successCount++;

        }


        System.out.println("\n" + "=".repeat(50));
        System.out.println("Successfully sent: " + successCount);
        if (failCount > 0) {
            System.out.println("Failed/Skipped: " + failCount);
        }
        System.out.println("=".repeat(50));

    }


    private String buildReminderEmail(User user, List<Loan> overdueLoans, int totalFine) {
        StringBuilder email = new StringBuilder();

        email.append("Dear ").append(user.getUsername()).append(",\n\n");
        email.append("This is a friendly reminder that you have overdue items from our library.\n\n");
        email.append("=".repeat(50)).append("\n");
        email.append("OVERDUE ITEMS:\n");
        email.append("=".repeat(50)).append("\n\n");

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


    public int getOverdueUsersCount() {
        return userOverdueLoans.size();
    }

    public Set<String> getOverdueUsernames() {
        return userOverdueLoans.keySet();
    }

}
