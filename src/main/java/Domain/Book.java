package Domain;

import javax.print.attribute.standard.Media;

/**
 * Represents a book, which is a specific type of media item.
 * Extends the MediaItem class and provides functionality specific to books,
 * such as managing ISBN, borrowing period, and fine calculation.
 *
 * @author Ahmad and Yahya
 */
public class Book extends MediaItem {

    private String isbn;

    /**
     * Constructs a new Book instance with the specified title, author, and ISBN.
     *
     * @param title the title of the book
     * @param author the author of the book
     * @param isbn the International Standard Book Number (ISBN) of the book
     */
    public Book(String title, String author, String isbn) {
        super(title, author);
        this.isbn = isbn;
    }

    /**
     * Retrieves the ISBN of the book.
     *
     * @return the ISBN of the book
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Gets the borrowing period for the book in days.
     * For this class, the period is set to 28 days.
     *
     * @return the number of days the book can be borrowed
     */
    @Override
    public int getBorrowingPeriodDays() {
        return 28;
    }

    /**
     * Gets the fine per day for a late return of the book.
     * For this class, the fine is set to 10 units per day.
     *
     * @return the fine per day for late return
     */
    @Override
    public int getFinePerDay() {
        return 10;
    }

    /**
     * Returns a string representation of the book, including the title, author,
     * ISBN, and availability status.
     *
     * @return a string representation of the book
     */
    @Override
    public String toString() {
        return ("Title:" + this.getTitle() + " | " + "Author:" + this.getAuthor() + " | " + "Isbn:" + this.isbn + " | " + (this.isAvailable() ? "Available" : "Borrowed"));
    }
}
