package Service;

import Domain.Book;

import java.util.List;

/**
 * The SearchByISBNStrategy class implements the BookSearchStrategy interface and provides
 * functionality to search for books based on their ISBN number.
 */
public class SearchByISBNStrategy implements BookSearchStrategy {

    /**
     * Searches for a book whose ISBN exactly matches the given query.
     *
     * @param books the list of books to search within
     * @param query the search query (ISBN number)
     * @return a list of books that exactly match the ISBN query (case-insensitive)
     */
    @Override
    public List<Book> search(List<Book> books, String query) {
        // Convert the query to lowercase for case-insensitive matching
        String q = query.toLowerCase();

        // Filter and return books where the ISBN exactly matches the query
        return books.stream()
                .filter(b -> b.getIsbn().toLowerCase().equals(q))
                .toList();
    }
}
