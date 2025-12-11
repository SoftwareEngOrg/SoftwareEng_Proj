package Service;

/**
 * Observer interface for receiving notifications when a book becomes available.
 */
public interface BookObserver {

    /**
     * Called when a book with the specified ISBN is available.
     *
     * @param isbn the ISBN of the available book
     */
    void onBookAvailable(String isbn);
}
