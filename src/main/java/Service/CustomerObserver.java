package Service;


import Domain.MediaItem;
import Domain.User;

public class CustomerObserver implements BookObserver {

    private final User user;
    private final EmailService emailService;
    private  String isbn;
    private final BookServiceCustomer service;

    public CustomerObserver(User user, EmailService emailService, BookServiceCustomer service) {
        this.user = user;
        this.emailService = emailService;
        this.service = service;
    }

    @Override
    public void onBookAvailable(String isbn) {
        this.isbn = isbn;
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            System.out.println("User " + user.getUsername() + " has no email, cannot send notification.");
            return;
        }

        MediaItem item = service.findMediaByIsbn(isbn);

        if (item == null) {
            System.out.println("Item not found for ISBN/ID: " + isbn);
            return;
        }

        String title = item.getTitle();
        String id = item.getIsbnOrId();

        String subject = "Media Item Is Now Available";
        String body =
                "Hello " + user.getUsername() +
                        "\n\nThe item you requested is now available." +
                        "\nTitle: \"" + title + "\" (ID: " + id + ")" +
                        "\n\nYou can borrow it now.";

        emailService.sendEmailAsync(user.getEmail(), subject, body);

    }
}
