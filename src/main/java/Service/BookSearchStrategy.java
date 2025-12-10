package Service;

import Domain.Book;

import java.util.List;
/**
 * Strategy interface for searching books.
 * Implementations define different search strategies.
 */
public interface BookSearchStrategy {
    /**
     * Searches for books based on the given query.
     *
     * @param books the list of books to search through
     * @param query the search query
     * @return a list of books that match the query
     */
    List<Book> search(List<Book> books, String query);
}
