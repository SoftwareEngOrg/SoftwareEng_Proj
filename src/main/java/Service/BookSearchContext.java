package Service;

import Domain.Book;

import java.util.List;

/**
 * Context class for executing book search using a chosen strategy.
 */
public class BookSearchContext {

    private BookSearchStrategy strategy;

    /**
     * Sets the search strategy to use.
     *
     * @param strategy the search strategy
     */
    public void setStrategy(BookSearchStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Executes the search using the current strategy.
     *
     * @param books the list of books to search
     * @param query the search query
     * @return the list of books matching the query
     */
    public List<Book> executeSearch(List<Book> books, String query) {
        return strategy.search(books, query);
    }
}
