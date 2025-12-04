package Service;
import Domain.Book;
import java.util.List;

public class BookService {

    protected FileBookRepository fileBook = FileBookRepository.getInstance();
    private final BookSearchContext searchContext = new BookSearchContext();


    public List<Book> searchByTitle(String title) {
        searchContext.setStrategy(new SearchByTitleStrategy());
        return searchContext.executeSearch(fileBook.findAllBooks(), title);
    }

    public List<Book> searchByAuthor(String author) {
        searchContext.setStrategy(new SearchByAuthorStrategy());
        return searchContext.executeSearch(fileBook.findAllBooks(), author);
    }

    public List<Book> searchByISBN(String isbn) {
        searchContext.setStrategy(new SearchByISBNStrategy());
        return searchContext.executeSearch(fileBook.findAllBooks(), isbn);
    }

}
