package Service;

import Domain.Book;

import java.util.List;

/**
 * The SearchByTitleStrategy class implements the BookSearchStrategy interface and provides
 * functionality to search for books based on their title.
 */
public class SearchByTitleStrategy implements BookSearchStrategy {

    /**
     * Searches for books whose titles contain the given query (case-insensitive).
     *
     * @param books the list of books to search within
     * @param query the search query (book title)
     * @return a list of books whose titles contain the query (case-insensitive)
     */
    @Override
    public List<Book> search(List<Book> books, String query) {
        // Convert the query to lowercase for case-insensitive matching
        String q = query.toLowerCase();

        // Filter and return books where the title contains the query
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q))
                .toList();
    }
}
