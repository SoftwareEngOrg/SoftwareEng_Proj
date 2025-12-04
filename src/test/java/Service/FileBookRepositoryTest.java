// File: src/test/java/Service/FileBookRepositoryTest.java

package Service;

import Domain.Book;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileBookRepositoryTest {

    private FileBookRepository repo;
    private Path tempFile;

    @BeforeAll
    void setupTempFile() throws IOException {
        // Create a temporary file instead of using "books.txt"
        tempFile = Files.createTempFile("test_books", ".txt");
        FileBookRepository.repoPath = tempFile.toString(); // Override the static path
    }

    @BeforeEach
    void setUp() {
        // Start fresh before each test
        repo = FileBookRepository.getInstance();
        repo.findAllBooks().clear(); // Clear cache
        try (PrintWriter pw = new PrintWriter(new FileWriter(tempFile.toString()))) {
            pw.print(""); // Clear file
        } catch (Exception ignored) {}
    }

    @AfterAll
    void cleanup() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    @DisplayName("saveBook() should add book to file and cache")
    void saveBook_addsBookToFileAndCache() {
        Book book = new Book("Clean Code", "Robert Martin", "111");

        repo.saveBook(book,1);


        assertEquals(1, repo.findAllBooks().size());
        assertEquals("Clean Code", repo.findAllBooks().get(0).getTitle());


        List<String> lines = readAllLines();
        assertEquals(1, lines.size());
        assertEquals("Clean Code;Robert Martin;111;true", lines.get(0));
    }

    @Test
    @DisplayName("loadBooksFromFile() should load books with availability")
    void loadBooksFromFile_loadsCorrectly() throws IOException {

        try (PrintWriter pw = new PrintWriter(new FileWriter(tempFile.toString()))) {
            pw.println("1984;George Orwell;999;true");
            pw.println("Java Book;Ahmed;123;false");
        }


        FileBookRepository newRepo = FileBookRepository.getInstance();

        List<Book> books = newRepo.findAllBooks();
        assertEquals(2, books.size());

        assertEquals("1984", books.get(0).getTitle());
        assertTrue(books.get(0).isAvailable());

        assertEquals("Java Book", books.get(1).getTitle());
        assertFalse(books.get(1).isAvailable());
    }

    @Test
    @DisplayName("updateBooks() should update availability in cache and file")
    void updateBooks_updatesAvailabilityCorrectly() throws IOException {

        Book originalBook = new Book("Harry Potter", "J.K. Rowling", "777");
        repo.saveBook(originalBook , 1);


        originalBook.setAvailable(false);
        repo.updateBooks(originalBook);


        Book cached = repo.findAllBooks().get(0);
        assertFalse(cached.isAvailable());


        List<String> lines = readAllLines();
        assertTrue(lines.get(0).endsWith(";false"));
    }

    @Test
    @DisplayName("Multiple books should all be saved and loaded correctly")
    void multipleBooks_workCorrectly() {
        Book b1 = new Book("Book A", "Author X", "100");
        Book b2 = new Book("Book B", "Author Y", "200");

        repo.saveBook(b1 , 1);
        repo.saveBook(b2 , 1);


        b1.setAvailable(false);
        repo.updateBooks(b1);


        FileBookRepository repo2 = FileBookRepository.getInstance();

        List<Book> loaded = repo2.findAllBooks();
        assertEquals(2, loaded.size());
        assertFalse(loaded.get(0).isAvailable());
        assertTrue(loaded.get(1).isAvailable());
    }


    private List<String> readAllLines() {
        try {
            return Files.readAllLines(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}