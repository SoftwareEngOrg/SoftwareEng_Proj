package Service;

import java.util.ArrayList;
import java.util.List;
/**
 * Singleton class that manages book inventory and observers.
 */
public class BookInventory
{

    private static BookInventory instance = new BookInventory();
    /**
     * Gets the singleton instance of the book inventory.
     *
     * @return the instance of the BookInventory
     */
    public static BookInventory getInstance()
    {
        return instance;
    }

    List<BookObserver> observers = new ArrayList<>();
    /**
     * Adds an observer to the inventory.
     *
     * @param observer the observer to be added
     */
    public void addObserver(BookObserver observer) {
        observers.add(observer);
    }
    /**
     * Notifies all observers when a book is returned.
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
