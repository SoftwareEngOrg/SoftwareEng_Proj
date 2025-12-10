package Service;
import Domain.Book;
import java.util.List;

/**
 * Service class for handling book-related operations, including search.
 * Uses the Strategy Pattern for different search strategies.
 */
public class BookService {

    protected FileBookRepository fileBook = FileBookRepository.getInstance();
    private final BookSearchContext searchContext = new BookSearchContext();

    /**
     * Searches for books by title.
     *
     * @param title the title to search for
     * @return a list of books matching the title
     */
    public List<Book> searchByTitle(String title) {
        searchContext.setStrategy(new SearchByTitleStrategy());
        return searchContext.executeSearch(fileBook.findAllBooks(), title);
    }
    /**
     * Searches for books by author.
     *
     * @param author the author to search for
     * @return a list of books by the specified author
     */
    public List<Book> searchByAuthor(String author) {
        searchContext.setStrategy(new SearchByAuthorStrategy());
        return searchContext.executeSearch(fileBook.findAllBooks(), author);
    }
    /**
     * Searches for books by ISBN.
     *
     * @param isbn the ISBN to search for
     * @return a list of books with the specified ISBN
     */
    public List<Book> searchByISBN(String isbn) {
        searchContext.setStrategy(new SearchByISBNStrategy());
        return searchContext.executeSearch(fileBook.findAllBooks(), isbn);
    }

}
