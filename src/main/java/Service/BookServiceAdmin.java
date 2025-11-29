package Service;

import Domain.Book;

import java.util.List;

public class BookServiceAdmin extends BookService {
    public boolean addBook(Book book) {
        FileBookRepository fileBook;
        List<Book> existingBooks = this.fileBook.findAllBooks();

        boolean exists = existingBooks.stream()
                .anyMatch(b -> b.getIsbn().equalsIgnoreCase(book.getIsbn()));

        if (exists) {
            return false;
        }
        this.fileBook.saveBook(book);
        return true;
    }
}
