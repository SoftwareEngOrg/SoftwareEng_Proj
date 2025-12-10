package Domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
/**
 * Represents a loan of a media item to a user.
 */
public class Loan {
    private String loanId;
    private User user;
    private MediaItem mediaItem;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    /**
     * Creates a new loan with the given details.
     *
     * @param loanId the ID of the loan
     * @param user the user borrowing the item
     * @param mediaItem the media item being borrowed
     * @param borrowDate the date the item is borrowed
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
     * Checks if the loan is overdue.
     *
     * @param currentDate the current date
     * @return true if the loan is overdue, false otherwise
     */
    public boolean isOverdue(LocalDate currentDate) {
        return returnDate == null && currentDate.isAfter(dueDate);
    }
    /**
     * Returns the number of overdue days.
     *
     * @param currentDate the current date
     * @return overdue days
     */
    public int getOverdueDays(LocalDate currentDate) {
        if (!isOverdue(currentDate)) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, currentDate);
    }
    /**
     * Calculates the fine based on overdue days.
     *
     * @param currentDate the current date
     * @return fine for overdue days
     */
    public int calculateFine(LocalDate currentDate) {
        int overdueDays = getOverdueDays(currentDate);
        return overdueDays * mediaItem.getFinePerDay();
    }
    /**
     * Marks the item as returned and updates its availability.
     *
     * @param returnDate the return date of the item
     */

    public void returnItem(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.mediaItem.setAvailable(true);
    }


    public String getLoanId() { return loanId; }
    public User getUser() { return user; }
    public MediaItem getMediaItem() { return mediaItem; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    /**
     * Returns a string representation of the loan.
     *
     * @return loan details as a string
     */

    @Override
    public String toString() {
        return String.format("Loan[%s]: %s borrowed by %s (Due: %s)",
                loanId, mediaItem.getTitle(), user.getUsername(), dueDate);
    }
}
