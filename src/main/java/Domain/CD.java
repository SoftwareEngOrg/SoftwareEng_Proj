package Domain;

import Domain.MediaItem;

/**
 * Represents a CD in the media collection.
 * Extends MediaItem and adds ISBN, borrowing period, and fine details.
 */
public class CD extends MediaItem {
    private String isbn;

    /**
     * Creates a new CD with the given title, author, and ISBN.
     *
     * @param title the title of the CD
     * @param author the author/artist of the CD
     * @param isbn the ISBN of the CD
     */
    public CD(String title, String author, String isbn) {
        super(title, author);
        this.isbn = isbn;
    }

    /**
     * Returns the ISBN of the CD.
     *
     * @return the ISBN of the CD
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Returns the borrowing period for the CD in days.
     *
     * @return borrowing period (7 days)
     */
    @Override
    public int getBorrowingPeriodDays() {
        return 7;
    }

    /**
     * Returns the fine per day for late returns of the CD.
     *
     * @return fine per day (20 units)
     */
    @Override
    public int getFinePerDay() {
        return 20;
    }

    /**
     * Returns a string representation of the CD.
     *
     * @return a string showing the title, author, ISBN, and availability status
     */
    @Override
    public String toString() {
        return "CD: " + this.getTitle() + " by " + this.getAuthor() + " | ISBN: " + isbn + " | " + (isAvailable() ? "Available" : "Borrowed");
    }
}
