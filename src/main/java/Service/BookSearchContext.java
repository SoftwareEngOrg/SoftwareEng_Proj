package Service;

import Domain.Book;

import java.util.List;

public class BookSearchContext {
    private BookSearchStrategy strategy;

    public void setStrategy(BookSearchStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Book> executeSearch(List<Book> books, String query) {
        return strategy.search(books, query);
    }
}
