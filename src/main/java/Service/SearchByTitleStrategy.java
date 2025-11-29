package Service;

import Domain.Book;

import java.util.List;

public class SearchByTitleStrategy implements BookSearchStrategy{
    @Override
    public List<Book> search(List<Book> books, String query) {
        String q = query.toLowerCase();

        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q))
                .toList();
    }
}
