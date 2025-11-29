package Service;

import Domain.Book;

import java.util.List;

public class SearchByAuthorStrategy implements BookSearchStrategy{
    @Override
    public List<Book> search(List<Book> books, String query) {
        String q = query.toLowerCase();

        return books.stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(q))
                .toList();
    }
}
