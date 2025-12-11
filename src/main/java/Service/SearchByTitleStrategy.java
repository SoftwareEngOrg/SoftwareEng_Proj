package Service;

import Domain.Book;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implements the {@link BookSearchStrategy} interface to search for books by their title.
 * <p>
 * This strategy performs a case-insensitive search by checking if the book's title contains
 * the provided query string.
 * </p>
 * <p>
 * Example usage:
 * <pre><code>
 * SearchByTitleStrategy strategy = new SearchByTitleStrategy();
 * List&lt;Book&gt; results = strategy.search(bookList, "harry potter");
 * </code></pre>
 * </p>
 *
 * @since 1.0
 */
public class SearchByTitleStrategy implements BookSearchStrategy {

    /**
     * Searches a list of books for titles that contain the given query string (case-insensitive).
     *
     * @param books the list of books to search through
     * @param query the search query string
     * @return a list of books whose titles contain the query string
     * @throws NullPointerException if {@code books} or {@code query} is {@code null}
     */
    @Override
    public List<Book> search(List<Book> books, String query) {
        Objects.requireNonNull(books, "books list cannot be null");
        Objects.requireNonNull(query, "query string cannot be null");

        String q = query.toLowerCase();

        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q))
                .toList();
    }
}
