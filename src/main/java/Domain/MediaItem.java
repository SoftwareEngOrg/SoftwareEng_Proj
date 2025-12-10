package Domain;

public abstract class MediaItem {
    private String title;
    private String author;
    private boolean available;
    /**
     * Creates a new media item with the specified title and author.
     *
     * @param title the title of the media item
     * @param author the author of the media item
     */

    public MediaItem(String title, String author) {
        this.title = title;
        this.author = author;
        this.available = true;
    }
    /**
     * Returns the borrowing period for the media item in days.
     * This method must be implemented by subclasses.
     *
     * @return borrowing period in days
     */
    public abstract int getBorrowingPeriodDays();

    /**
     * Returns the fine per day for a late return of the media item.
     * This method must be implemented by subclasses.
     *
     * @return fine per day
     */

    public abstract int getFinePerDay();


    public String getTitle() { return title; }
    public  void setTitle(String T)
    {
         this.title = T ;
    }

    public String getAuthor() { return author; }
    public void setAuthor(String A)
    {
        this.author = A;
    }

    public boolean isAvailable() { return available; }

    public void setAvailable(boolean available) { this.available = available; }
    /**
     * Returns the ISBN or ID of the media item.
     * For books, it returns the ISBN; for CDs, the ISBN;
     * for other media types, returns a unique identifier.
     *
     * @return ISBN or unique ID
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
     * Returns a string representation of the media item.
     *
     * @return media item details
     */
    @Override
    public String toString() {
        return String.format("%s: %s by %s [%s]",
                getClass().getSimpleName(), title, author,
                isAvailable() ? "Available" : "Borrowed");
    }
}
