package Domain;

public abstract class MediaItem {
    private String title;
    private String author; // or artist for CDs
    private boolean available;
    public MediaItem(String title, String author) {
        this.title = title;
        this.author = author;
        this.available = true;
    }
    // Abstract method - each media type has different borrowing period
    public abstract int getBorrowingPeriodDays();

    // Abstract method - each media type has different fine strategy
    public abstract int getFinePerDay();
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getIsbnOrId() {
        if (this instanceof Book book) {
            return book.getIsbn();
        } else if (this instanceof CD cd) {
            return cd.getIsbn();
        }
        return "UNKNOWN-" + hashCode();
    }
    @Override
    public String toString() {
        return String.format("%s: %s by %s [%s]",
                getClass().getSimpleName(), title, author,
                available ? "Available" : "Borrowed");
    }
}
