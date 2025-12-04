package Domain;

public abstract class MediaItem {
    private String title;
    private String author;
    private boolean available;


    public MediaItem(String title, String author) {
        this.title = title;
        this.author = author;
        this.available = true;
    }

    public abstract int getBorrowingPeriodDays();


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
