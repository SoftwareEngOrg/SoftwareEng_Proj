package Service;

import Domain.Book;

import java.util.List;
import java.util.Objects;


/**
 * Searches for books by their ISBN.
 */
public class SearchByISBNStrategy implements BookSearchStrategy {

    /**
     * Searches a list of books for a book with the exact ISBN (case-insensitive).
     *
     * @param books the list of books to search
     * @param query the ISBN to search for
     * @return a list of books matching the ISBN
     * @throws NullPointerException if {@code books} or {@code query} is {@code null}
     */
    @Override
    public List<Book> search(List<Book> books, String query) {
        Objects.requireNonNull(books, "books list cannot be null");
        Objects.requireNonNull(query, "query string cannot be null");

        String q = query.toLowerCase();

        return books.stream()
                .filter(b -> b.getIsbn().toLowerCase().equals(q))
                .toList();
    }
}
