package Service;
import Domain.Book;
import java.util.List;

public class BookService {

    private FileBookRepository fileBook = new FileBookRepository();

    public void addBook(Book book)
    {
        fileBook.saveBook(book);
    }

    public List<Book> searchByTitle(String title)
    {
        return fileBook.searchBooks("title", title);
    }

    public List<Book> searchByAuthor(String author)
    {
        return fileBook.searchBooks("author", author);
    }

    public List<Book> searchByISBN(String isbn)
    {
        return fileBook.searchBooks("isbn", isbn);
    }

}
