package Service;

import Domain.Book;

import java.util.List;

/**
 * Strategy interface for searching books.
 */
public interface BookSearchStrategy {

    /**
     * Searches a list of books based on a query.
     *
     * @param books the list of books to search
     * @param query the search query
     * @return a list of books matching the query
     */
    List<Book> search(List<Book> books, String query);
}
