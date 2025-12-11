package Domain;

/**
 * Represents a generic media item in the library system.
 * This abstract class provides common fields and behaviors
 * shared by different media types such as books and CDs.
 *
 * <p>Subclasses must define borrowing period and fine amounts.</p>
 *
 * <p>Example usage:</p>
 * <pre><code>
 * MediaItem item = new Book("Title", "Author", "ISBN123");
 * System.out.println(item.getTitle());
 * </code></pre>
 *
 * @since 1.0
 */
public abstract class MediaItem {
    private String title;
    private String author;
    private boolean available;

    /**
     * Creates a new media item with the specified title and author.
     * Items are marked available by default.
     *
     * @param title  the title of the media item
     * @param author the author or creator of the media item
     */
    public MediaItem(String title, String author) {
        this.title = title;
        this.author = author;
        this.available = true;
    }

    /**
     * Returns the number of days this item may be borrowed.
     * Must be implemented by subclasses.
     *
     * @return the borrowing period in days
     */
    public abstract int getBorrowingPeriodDays();

    /**
     * Returns the fine charged per overdue day for this item.
     * Must be implemented by subclasses.
     *
     * @return the fine per day
     */
    public abstract int getFinePerDay();

    /**
     * Returns the title of this media item.
     *
     * @return the title
     */
    public String getTitle() { return title; }

    /**
     * Updates the title of this media item.
     *
     * @param title the new title
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Returns the author or creator of this media item.
     *
     * @return the author
     */
    public String getAuthor() { return author; }

    /**
     * Updates the author of this media item.
     *
     * @param author the new author
     */
    public void setAuthor(String author) { this.author = author; }

    /**
     * Returns whether this item is currently available for borrowing.
     *
     * @return true if available, otherwise false
     */
    public boolean isAvailable() { return available; }

    /**
     * Sets whether the item is available for borrowing.
     *
     * @param available true if available, false if borrowed
     */
    public void setAvailable(boolean available) { this.available = available; }

    /**
     * Returns an identifier for this media item.
     * For books and CDs, this returns their ISBN.
     * For unknown types, it returns a generated ID.
     *
     * @return the ISBN or fallback identifier
     */
    public String getIsbnOrId() {
        if (this instanceof Book book) {
            return book.getIsbn();
        } else if (this instanceof CD cd) {
            return cd.getIsbn();
        }
        return "UNKNOWN-" + hashCode();
    }

    /**
     * Returns a generic string representation of the media item.
     *
     * @return a formatted string including class name, title, author, and availability
     */
    @Override
    public String toString() {
        return String.format(
                "%s: %s by %s [%s]",
                getClass().getSimpleName(), title, author,
                isAvailable() ? "Available" : "Borrowed"
        );
    }
}
