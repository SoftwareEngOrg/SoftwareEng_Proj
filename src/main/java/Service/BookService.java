package Service;

import Domain.Book;
import java.util.List;

/**
 * Service class for searching books using different strategies.
 */
public class BookService {

    protected FileBookRepository fileBook = FileBookRepository.getInstance();
    private final BookSearchContext searchContext = new BookSearchContext();

    /**
     * Searches books by title.
     *
     * @param title the book title to search for
     * @return a list of books matching the title
     */
    public List<Book> searchByTitle(String title) {
        searchContext.setStrategy(new SearchByTitleStrategy());
        return searchContext.executeSearch(fileBook.findAllBooks(), title);
    }

    /**
     * Searches books by author.
     *
     * @param author the author name to search for
     * @return a list of books matching the author
     */
    public List<Book> searchByAuthor(String author) {
        searchContext.setStrategy(new SearchByAuthorStrategy());
        return searchContext.executeSearch(fileBook.findAllBooks(), author);
    }

    /**
     * Searches books by ISBN.
     *
     * @param isbn the ISBN to search for
     * @return a list of books matching the ISBN
     */
    public List<Book> searchByISBN(String isbn) {
        searchContext.setStrategy(new SearchByISBNStrategy());
        return searchContext.executeSearch(fileBook.findAllBooks(), isbn);
    }
}
