package Service;

import Domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BookServiceTest {

    private void injectMockRepo(BookService service, FileBookRepository mockRepo) throws Exception {
        Field f = BookService.class.getDeclaredField("fileBook");
        f.setAccessible(true);
        f.set(service, mockRepo);
    }

    private void injectMockContext(BookService service, BookSearchContext ctx) throws Exception {
        Field f = BookService.class.getDeclaredField("searchContext");
        f.setAccessible(true);
        f.set(service, ctx);
    }

    @Test
    @DisplayName("searchByTitle returns expected mocked result")
    void testSearchByTitle() throws Exception {
        BookService service = new BookService();

        FileBookRepository repo = mock(FileBookRepository.class);
        BookSearchContext ctx = mock(BookSearchContext.class);

        injectMockRepo(service, repo);
        injectMockContext(service, ctx);

        List<Book> list = List.of(new Book("T","A","1"));
        when(repo.findAllBooks()).thenReturn(list);
        when(ctx.executeSearch(list, "abc")).thenReturn(list);

        List<Book> result = service.searchByTitle("abc");

        assertEquals(list, result);
    }

    @Test
    @DisplayName("searchByAuthor returns expected mocked result")
    void testSearchByAuthor() throws Exception {
        BookService service = new BookService();

        FileBookRepository repo = mock(FileBookRepository.class);
        BookSearchContext ctx = mock(BookSearchContext.class);

        injectMockRepo(service, repo);
        injectMockContext(service, ctx);

        List<Book> list = List.of(new Book("T","A","1"));
        when(repo.findAllBooks()).thenReturn(list);
        when(ctx.executeSearch(list, "xyz")).thenReturn(list);

        List<Book> result = service.searchByAuthor("xyz");

        assertEquals(list, result);
    }

    @Test
    @DisplayName("searchByISBN returns expected mocked result")
    void testSearchByISBN() throws Exception {
        BookService service = new BookService();

        FileBookRepository repo = mock(FileBookRepository.class);
        BookSearchContext ctx = mock(BookSearchContext.class);

        injectMockRepo(service, repo);
        injectMockContext(service, ctx);

        List<Book> list = List.of(new Book("T","A","1"));
        when(repo.findAllBooks()).thenReturn(list);
        when(ctx.executeSearch(list, "111")).thenReturn(list);

        List<Book> result = service.searchByISBN("111");

        assertEquals(list, result);
    }
}
