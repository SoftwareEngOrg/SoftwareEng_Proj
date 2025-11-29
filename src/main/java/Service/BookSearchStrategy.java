package Service;

import Domain.Book;

import java.util.List;

public interface BookSearchStrategy {
    List<Book> search(List<Book> books, String query);
}
