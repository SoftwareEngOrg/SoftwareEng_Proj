package Service;

import Domain.User;

public class CustomerObserver implements BookObserver {

    private final User user;
    private final EmailService emailService;

    public CustomerObserver(User user, EmailService emailService) {
        this.user = user;
        this.emailService = emailService;
    }

    @Override
    public void onBookAvailable(String isbn) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            System.out.println("User " + user.getUsername() + " has no email, cannot send notification.");
            return;
        }

        String subject = "Book Is Now Available";
        String body =
                "Hello " + user.getUsername() +
                        "\n\nThe book you requested is now available.\nISBN: " + isbn +
                        "\n\nYou can borrow it now.";

        emailService.sendEmail(user.getEmail(), subject, body);
    }
}
