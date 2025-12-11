package Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class that manages book inventory and notifies observers when a book is returned.
 */
public class BookInventory {

    private static BookInventory instance = new BookInventory();

    /**
     * Returns the single instance of BookInventory.
     *
     * @return the BookInventory instance
     */
    public static BookInventory getInstance() {
        return instance;
    }

    List<BookObserver> observers = new ArrayList<>();

    /**
     * Adds an observer to be notified when a book is returned.
     *
     * @param observer the observer to add
     */
    public void addObserver(BookObserver observer) {
        observers.add(observer);
    }

    /**
     * Notifies all observers that a book with the given ISBN has been returned.
     * Clears the observer list after notification.
     *
     * @param isbn the ISBN of the returned book
     */
    public void notifyBookReturned(String isbn) {
        System.out.println("Book returned: " + isbn);
        for (BookObserver observer : observers) {
            observer.onBookAvailable(isbn);
        }
        observers.clear();
    }
}
