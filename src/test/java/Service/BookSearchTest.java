
package Service;

import Domain.Book;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookSearchTest {

    private BookService bookService;
    private Path tempBookFile;

    @BeforeAll
    void setUpTestFile() throws IOException {
        tempBookFile = Files.createTempFile("books_test", ".txt");
        FileBookRepository.repoPath = tempBookFile.toString();
    }

    @BeforeEach
    void init() throws IOException {

        String testData = """
            Clean Code;Robert Martin;978-0134685991;true
            The Pragmatic Programmer;Andrew Hunt;978-0201616224;true
            Design Patterns;Erich Gamma;978-0201633610;true
            Java Concurrency in Practice;Brian Goetz;978-0321349606;true
            Effective Java;Joshua Bloch;978-0134685991;true
            """;

        Files.writeString(tempBookFile, testData);


        FileBookRepository.reset();


        bookService = new BookService();
    }

    @AfterAll
    void cleanup() throws IOException {
        Files.deleteIfExists(tempBookFile);

        FileBookRepository.reset();
    }

    @Test
    @DisplayName("searchByTitle finds books with partial title match (case insensitive)")
    void searchByTitle_partialMatch_caseInsensitive() {
        List<Book> results = bookService.searchByTitle("clean");

        assertEquals(1, results.size());
        assertEquals("Clean Code", results.getFirst().getTitle());

        results = bookService.searchByTitle("JAVA");
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("searchByTitle returns empty list when no match")
    void searchByTitle_noMatch_returnsEmpty() {
        assertTrue(bookService.searchByTitle("Harry Potter").isEmpty());
    }

    @Test
    @DisplayName("searchByAuthor finds books with partial author name (case insensitive)")
    void searchByAuthor_partialMatch_caseInsensitive() {
        List<Book> results = bookService.searchByAuthor("martin");

        assertEquals(1, results.size());
        assertEquals("Robert Martin", results.getFirst().getAuthor());

        results = bookService.searchByAuthor("gamma");
        assertEquals(1, results.size());
        assertTrue(results.get(0).getTitle().contains("Design Patterns"));
    }

    @Test
    @DisplayName("searchByAuthor returns empty when no match")
    void searchByAuthor_noMatch_returnsEmpty() {
        assertTrue(bookService.searchByAuthor("J.K. Rowling").isEmpty());
    }

    @Test
    @DisplayName("searchByISBN finds exact match only (case insensitive)")
    void searchByISBN_exactMatch_caseInsensitive() {
        List<Book> results = bookService.searchByISBN("978-0134685991");

        assertEquals(2, results.size());

        results = bookService.searchByISBN("978-0201616224");
        assertEquals(1, results.size());
        assertEquals("The Pragmatic Programmer", results.get(0).getTitle());

        results = bookService.searchByISBN("978-0134685991".toUpperCase());
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("searchByISBN returns empty when no match")
    void searchByISBN_noMatch_returnsEmpty() {
        assertTrue(bookService.searchByISBN("000-0000000000").isEmpty());
    }

    @Test
    @DisplayName("Multiple strategies work correctly through BookService")
    void bookService_usesDifferentStrategies_correctly() {

        assertTrue(bookService.searchByTitle("effective").size() == 1);


        assertTrue(bookService.searchByAuthor("Bloch").size() == 1);


        assertTrue(bookService.searchByISBN("978-0321349606").size() == 1);
    }

    @Test
    @DisplayName("BookSearchContext executes correct strategy")
    void bookSearchContext_executesStrategy() {
        BookSearchContext context = new BookSearchContext();

        List<Book> allBooks = FileBookRepository.getInstance().findAllBooks();

        context.setStrategy(new SearchByTitleStrategy());
        assertFalse(context.executeSearch(allBooks, "clean").isEmpty());

        context.setStrategy(new SearchByAuthorStrategy());
        assertFalse(context.executeSearch(allBooks, "goetz").isEmpty());

        context.setStrategy(new SearchByISBNStrategy());
        assertFalse(context.executeSearch(allBooks, "978-0201633610").isEmpty());
    }

    @Test
    @DisplayName("All strategies handle empty book list")
    void allStrategies_handleEmptyList() {
        List<Book> emptyList = List.of();

        BookSearchContext context = new BookSearchContext();

        context.setStrategy(new SearchByTitleStrategy());
        assertTrue(context.executeSearch(emptyList, "anything").isEmpty());

        context.setStrategy(new SearchByAuthorStrategy());
        assertTrue(context.executeSearch(emptyList, "anyone").isEmpty());

        context.setStrategy(new SearchByISBNStrategy());
        assertTrue(context.executeSearch(emptyList, "123").isEmpty());
    }
}