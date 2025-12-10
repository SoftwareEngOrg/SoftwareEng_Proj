package Service;

import Domain.Book;

import java.util.List;

/**
 * The SearchByAuthorStrategy class implements the BookSearchStrategy interface and provides
 * functionality to search for books based on the author's name.
 */
public class SearchByAuthorStrategy implements BookSearchStrategy {

    /**
     * Searches for books whose author's name contains the given query.
     *
     * @param books the list of books to search within
     * @param query the search query (author's name or part of it)
     * @return a list of books whose author name contains the query string (case-insensitive)
     */
    @Override
    public List<Book> search(List<Book> books, String query) {
        // Convert the query to lowercase for case-insensitive matching
        String q = query.toLowerCase();

        // Filter and return books where the author's name contains the query
        return books.stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(q))
                .toList();
    }
}
