package Domain;

import Domain.MediaItem;

/**
 * Represents a CD in the library system.
 * A CD is a type of MediaItem and includes an ISBN identifier.
 *
 * <p>Example usage:</p>
 * <pre><code>
 * CD cd = new CD("Thriller", "Michael Jackson", "987654321");
 * System.out.println(cd.getIsbn());
 * </code></pre>
 *
 * @since 1.0
 */
public class CD extends MediaItem {

    private String isbn;

    /**
     * Creates a new CD with the specified title, author, and ISBN.
     *
     * @param title  the title of the CD
     * @param author the creator or artist of the CD
     * @param isbn   the ISBN or unique identifier of the CD
     */
    public CD(String title, String author, String isbn) {
        super(title, author);
        this.isbn = isbn;
    }

    /**
     * Returns the ISBN of this CD.
     *
     * @return the ISBN value
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Returns the borrowing period for CDs.
     *
     * @return 7 days
     */
    @Override
    public int getBorrowingPeriodDays() {
        return 7;
    }

    /**
     * Returns the fine amount per overdue day for CDs.
     *
     * @return the fine per day (20)
     */
    @Override
    public int getFinePerDay() {
        return 20;
    }

    /**
     * Returns a formatted string describing this CD.
     *
     * @return a string with title, author, ISBN, and availability status
     */
    @Override
    public String toString() {
        return "CD: " + this.getTitle() + " by " + this.getAuthor() +
                " | ISBN: " + isbn + " | " +
                (isAvailable() ? "Available" : "Borrowed");
    }
}
