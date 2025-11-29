package Domain;

import javax.print.attribute.standard.Media;

public class Book extends MediaItem {

    private String isbn;

    public Book(String title, String author, String isbn) {
        super(title, author); // Call the constructor of MediaItem
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }
    @Override
    public int getBorrowingPeriodDays() {
        return 28;
    }

    @Override
    public int getFinePerDay() {
        return 10;
    }

    @Override
    public String toString()
    {
        return ("Title:" + this.getTitle() + " | " + "Author:" + getAuthor() + " | " + "Isbn:" + isbn + " | " + isAvailable());
    }

}
