package Service;

import Domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("BookSearchContext Tests")
class BookSearchContextTest {

    private BookSearchContext context;
    private BookSearchStrategy strategy;

    @BeforeEach
    void setUp() {
        context = new BookSearchContext();
        strategy = mock(BookSearchStrategy.class);
    }

    @Test
    @DisplayName("Set strategy should store strategy")
    void testSetStrategyStoresStrategy() {
        context.setStrategy(strategy);
        assertNotNull(context.executeSearch(List.of(), ""));
    }

    @Test
    @DisplayName("Execute search should call strategy search")
    void testExecuteSearchCallsStrategy() {
        List<Book> books = List.of(new Book("123", "Title", "Author"));
        when(strategy.search(books, "Title")).thenReturn(books);

        context.setStrategy(strategy);
        List<Book> result = context.executeSearch(books, "Title");

        verify(strategy, times(1)).search(books, "Title");
        assertEquals(books, result);
    }
}
