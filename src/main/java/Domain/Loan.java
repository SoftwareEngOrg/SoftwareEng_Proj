package Domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {
    private String loanId;
    private User user;
    private MediaItem mediaItem;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(String loanId, User user, MediaItem mediaItem, LocalDate borrowDate) {
        this.loanId = loanId;
        this.user = user;
        this.mediaItem = mediaItem;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(mediaItem.getBorrowingPeriodDays());
        this.returnDate = null;
    }

    public boolean isOverdue(LocalDate currentDate) {
        return returnDate == null && currentDate.isAfter(dueDate);
    }

    public int getOverdueDays(LocalDate currentDate) {
        if (!isOverdue(currentDate)) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, currentDate);
    }

    public int calculateFine(LocalDate currentDate) {
        int overdueDays = getOverdueDays(currentDate);
        return overdueDays * mediaItem.getFinePerDay();
    }

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


    @Override
    public String toString() {
        return String.format("Loan[%s]: %s borrowed by %s (Due: %s)",
                loanId, mediaItem.getTitle(), user.getUsername(), dueDate);
    }
}
