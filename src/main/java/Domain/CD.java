package Domain;

import Domain.MediaItem;

public class CD extends MediaItem {
    private String isbn;

    public CD(String title, String author, String isbn) {
        super(title, author);
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public int getBorrowingPeriodDays() {
        return 7;
    }

    @Override
    public int getFinePerDay() {
        return 20;
    }

    @Override
    public String toString() {
        return "CD: " + this.getTitle() + " by " + this.getAuthor() + " | ISBN: " + isbn + " | " + (isAvailable() ? "Available" : "Borrowed");
    }
}