package Domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a loan transaction in the library system.
 * A Loan tracks which user borrowed which media item, as well as
 * borrowing date, due date, and return date.
 *
 * <p>Example usage:</p>
 * <pre><code>
 * Loan loan = new Loan("L001", user, mediaItem, LocalDate.now());
 * boolean overdue = loan.isOverdue(LocalDate.now());
 * int fine = loan.calculateFine(LocalDate.now());
 * </code></pre>
 *
 * @since 1.0
 */
public class Loan {
    private String loanId;
    private User user;
    private MediaItem mediaItem;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    /**
     * Creates a new loan record for a user and a media item.
     * The due date is automatically calculated based on the item's borrowing period.
     *
     * @param loanId     unique identifier of the loan
     * @param user       the user who borrowed the item
     * @param mediaItem  the media item being borrowed
     * @param borrowDate the date the loan begins
     */
    public Loan(String loanId, User user, MediaItem mediaItem, LocalDate borrowDate) {
        this.loanId = loanId;
        this.user = user;
        this.mediaItem = mediaItem;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(mediaItem.getBorrowingPeriodDays());
        this.returnDate = null;
    }

    /**
     * Checks if this loan is overdue based on the given current date.
     *
     * @param currentDate the date used to evaluate overdue status
     * @return true if the item is not returned and currentDate is after the due date
     */
    public boolean isOverdue(LocalDate currentDate) {
        return returnDate == null && currentDate.isAfter(dueDate);
    }

    /**
     * Calculates the number of days the loan is overdue.
     *
     * @param currentDate the date used for the calculation
     * @return the number of overdue days, or 0 if not overdue
     */
    public int getOverdueDays(LocalDate currentDate) {
        if (!isOverdue(currentDate)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(dueDate, currentDate);
    }

    /**
     * Calculates the fine owed for this loan based on overdue days.
     *
     * @param currentDate the date used to calculate the fine
     * @return the total fine amount
     */
    public int calculateFine(LocalDate currentDate) {
        int overdueDays = getOverdueDays(currentDate);
        return overdueDays * mediaItem.getFinePerDay();
    }

    /**
     * Marks the media item as returned and updates the return date.
     *
     * @param returnDate the date the item was returned
     */
    public void returnItem(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.mediaItem.setAvailable(true);
    }

    /** @return the loan ID */
    public String getLoanId() { return loanId; }

    /** @return the user who borrowed the item */
    public User getUser() { return user; }

    /** @return the borrowed media item */
    public MediaItem getMediaItem() { return mediaItem; }

    /** @return the borrow date */
    public LocalDate getBorrowDate() { return borrowDate; }

    /** @return the due date */
    public LocalDate getDueDate() { return dueDate; }

    /** @return the return date, or null if not returned yet */
    public LocalDate getReturnDate() { return returnDate; }

    /**
     * Returns a formatted string describing the loan.
     *
     * @return a string showing basic loan information
     */
    @Override
    public String toString() {
        return String.format(
                "Loan[%s]: %s borrowed by %s (Due: %s)",
                loanId, mediaItem.getTitle(), user.getUsername(), dueDate
        );
    }
}
