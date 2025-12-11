package Service;

import Domain.Book;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Searches for books by author name.
 */
public class SearchByAuthorStrategy implements BookSearchStrategy {

    /**
     * Searches a list of books for authors containing the given query (case-insensitive).
     *
     * @param books the list of books to search
     * @param query the author name to search for
     * @return a list of books with matching authors
     * @throws NullPointerException if {@code books} or {@code query} is {@code null}
     */
    @Override
    public List<Book> search(List<Book> books, String query) {
        Objects.requireNonNull(books, "books list cannot be null");
        Objects.requireNonNull(query, "query string cannot be null");

        String q = query.toLowerCase();

        return books.stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(q))
                .toList();
    }
}
