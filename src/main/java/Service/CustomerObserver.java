package Service;

import Domain.Book;
import Domain.Loan;
import Domain.MediaItem;
import Domain.User;
/**
 * CustomerObserver is an implementation of the BookObserver interface.
 * It is responsible for notifying users via email when a media item (Book or CD)
 * that they were waiting for becomes available.
 * <p>
 * This observer listens for changes in the availability of books (or other media items)
 * and sends an email notification to the user if their requested media item becomes available.
 * </p>
 */
public class CustomerObserver implements BookObserver {

    private final User user;
    private final EmailService emailService;
    private  String isbn;
    private final BookServiceCustomer service;
    /**
     * Constructs a CustomerObserver that will notify the specified user
     * when the media item they requested becomes available.
     *
     * @param user the user who will receive the notification
     * @param emailService the email service used to send the notification
     * @param service the book service that helps in finding the media item by ISBN
     */
    public CustomerObserver(User user, EmailService emailService, BookServiceCustomer service) {
        this.user = user;
        this.emailService = emailService;
        this.service = service;
    }
    /**
     * This method is triggered when a media item (Book or CD) becomes available.
     * It sends an email notification to the user with the title and ID of the item.
     * If the user doesn't have an email or the item is not found, an error message is printed.
     *
     * @param isbn the ISBN (or ID) of the media item that has become available
     */
    @Override
    public void onBookAvailable(String isbn) {
        this.isbn = isbn;
        // Check if the user has a valid email address
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            System.out.println("User " + user.getUsername() + " has no email, cannot send notification.");
            return;
        }
        // Find the media item using the ISBN
        MediaItem item = service.findMediaByIsbn(isbn);
        // Check if the item exists
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
