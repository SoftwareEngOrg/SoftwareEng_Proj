package Service;

import Domain.Book;

import java.util.List;

/**
 * Context class for executing book search using a specific search strategy.
 */
public class BookSearchContext {


    private BookSearchStrategy strategy;
    /**
     * Sets the search strategy to be used.
     *
     * @param strategy the strategy to use for searching
     */
    public void setStrategy(BookSearchStrategy strategy) {
        this.strategy = strategy;
    }
    /**
     * Executes the search based on the current strategy.
     *
     * @param books the list of books to search through
     * @param query the search query
     * @return the list of books that match the query based on the strategy
     */
    public List<Book> executeSearch(List<Book> books, String query) {
        return strategy.search(books, query);
    }
}
