package Domain;


/**
 * Represents a book in the library system.
 * A Book is a type of MediaItem and includes an ISBN.
 *
 * <p>Example usage:</p>
 * <pre><code>
 * Book b = new Book("1984", "George Orwell", "1234567890");
 * System.out.println(b.getIsbn());
 * </code></pre>
 *
 * @since 1.0
 */
public class Book extends MediaItem {

    private String isbn;

    /**
     * Creates a new Book with the given title, author, and ISBN.
     *
     * @param title  the title of the book
     * @param author the author of the book
     * @param isbn   the ISBN identifier of the book
     */
    public Book(String title, String author, String isbn) {
        super(title, author);
        this.isbn = isbn;
    }

    /**
     * Returns the ISBN of this book.
     *
     * @return the ISBN value
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Returns the borrowing period for books.
     *
     * @return 28 days
     */
    @Override
    public int getBorrowingPeriodDays() {
        return 28;
    }

    /**
     * Returns the fine amount per overdue day for books.
     *
     * @return the fine per day (10)
     */
    @Override
    public int getFinePerDay() {
        return 10;
    }

    /**
     * Returns a formatted string containing book details.
     *
     * @return a string representation of the book including
     *         title, author, ISBN, and availability status
     */
    @Override
    public String toString() {
        return ("Title:" + this.getTitle() + " | " +
                "Author:" + this.getAuthor() + " | " +
                "Isbn:" + this.isbn + " | " +
                (this.isAvailable() ? "Available" : "Borrowed"));
    }

}
