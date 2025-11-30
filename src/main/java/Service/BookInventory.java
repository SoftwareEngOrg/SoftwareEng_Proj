package Service;

import java.util.ArrayList;
import java.util.List;

public class BookInventory
{

    private static BookInventory instance = new BookInventory();

    public static BookInventory getInstance()
    {
        return instance;
    }

    private List<BookObserver> observers = new ArrayList<>();

    public void addObserver(BookObserver observer) {
        observers.add(observer);
    }


    public void notifyBookReturned(String isbn)
    {
        System.out.println("Book returned: " + isbn);
        for (BookObserver observer : observers) {
            observer.onBookAvailable(isbn);
        }
        observers.clear();
    }
}
