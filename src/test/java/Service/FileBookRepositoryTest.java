package Service;

import Domain.Book;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class FileBookRepositoryTest {

    private static final String TEST_ISBN = "1234567890";
    private static final Book SAMPLE_BOOK = new Book("Test Book", "Author", TEST_ISBN);

    @TempDir
    Path tempDir;
    private Path testFile;

    @BeforeEach
    void setUp() throws Exception {
        testFile = tempDir.resolve("books.txt");

        resetStaticField("instance", null);
        resetStaticField("repoPath", testFile.toString());
        resetStaticField("cachedBooks", new ArrayList<>());
    }

    @AfterEach
    void tearDown() throws Exception {
        resetStaticField("repoPath", "books.txt");
        resetStaticField("instance", null);
    }

    private void resetStaticField(String fieldName, Object value) throws Exception {
        Field field = FileBookRepository.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    @Test
    void saveBook_appendsBookToFileAndCache() throws Exception {

        try (MockedStatic<FileMediaCopyRepository> mocked = mockStatic(FileMediaCopyRepository.class)) {

            mocked.when(FileMediaCopyRepository::getInstance)
                    .thenReturn(mock(FileMediaCopyRepository.class));

            FileBookRepository.saveBook(SAMPLE_BOOK, 3);

            List<String> lines = Files.readAllLines(testFile);
            assertEquals(1, lines.size());
            String line = lines.get(0);
            assertEquals("Test Book;Author;1234567890;true", line);

            FileBookRepository repo = FileBookRepository.getInstance();
            assertEquals(1, repo.findAllBooks().size());
            assertEquals("Test Book", repo.findAllBooks().get(0).getTitle());
        }
    }

    @Test
    void findAllBooks_returnsCopyOfCachedList() throws Exception {
        try (MockedStatic<FileMediaCopyRepository> mocked = mockStatic(FileMediaCopyRepository.class)) {
            mocked.when(FileMediaCopyRepository::getInstance).thenReturn(mock(FileMediaCopyRepository.class));

            FileBookRepository.saveBook(SAMPLE_BOOK, 1);

            FileBookRepository repo = FileBookRepository.getInstance();
            List<Book> list1 = repo.findAllBooks();
            List<Book> list2 = repo.findAllBooks();

            assertEquals(1, list1.size());
            assertNotSame(list1, list2);
            list1.clear();
            assertEquals(1, repo.findAllBooks().size());
        }
    }

    @Test
    void findByIsbn_returnsCorrectBook_ignoreCase() throws Exception {
        try (MockedStatic<FileMediaCopyRepository> mocked = mockStatic(FileMediaCopyRepository.class)) {
            mocked.when(FileMediaCopyRepository::getInstance).thenReturn(mock(FileMediaCopyRepository.class));

            FileBookRepository.saveBook(SAMPLE_BOOK, 1);
            FileBookRepository repo = FileBookRepository.getInstance();

            assertEquals("Test Book", repo.findByIsbn("1234567890").getTitle());
            assertEquals("Test Book", repo.findByIsbn(" 1234567890 ").getTitle());
            assertEquals("Test Book", repo.findByIsbn("1234567890".toLowerCase()).getTitle());
            assertNull(repo.findByIsbn("wrong"));
        }
    }

    @Test
    void updateBookAvailability_updatesAvailabilityBasedOnCopies() throws Exception {
        FileMediaCopyRepository mockCopies = mock(FileMediaCopyRepository.class);

        try (MockedStatic<FileMediaCopyRepository> mocked = mockStatic(FileMediaCopyRepository.class)) {
            mocked.when(FileMediaCopyRepository::getInstance).thenReturn(mockCopies);


            Mockito.when(mockCopies.getAvailableCopiesCount(TEST_ISBN)).thenReturn(2);
            FileBookRepository.saveBook(SAMPLE_BOOK, 2);

            FileBookRepository repo = FileBookRepository.getInstance();
            assertTrue(repo.findByIsbn(TEST_ISBN).isAvailable());


            Mockito.when(mockCopies.getAvailableCopiesCount(TEST_ISBN)).thenReturn(0);
            repo.updateBookAvailability(TEST_ISBN);
            assertFalse(repo.findByIsbn(TEST_ISBN).isAvailable());


            Mockito.when(mockCopies.getAvailableCopiesCount(TEST_ISBN)).thenReturn(1);
            repo.updateBookAvailability(TEST_ISBN);
            assertTrue(repo.findByIsbn(TEST_ISBN).isAvailable());
        }
    }

    @Test
    void reloadBooks_refreshesCacheFromFile() throws Exception {
        Files.write(testFile, List.of(
                "Harry Potter;J.K. Rowling;9999999999;true",
                "1984;George Orwell;1111111111;false"
        ));

        resetStaticField("cachedBooks", new ArrayList<>());

        FileBookRepository repo = FileBookRepository.getInstance();
        repo.reloadBooks();

        List<Book> books = repo.findAllBooks();
        assertEquals(2, books.size());
        assertEquals("Harry Potter", books.get(0).getTitle());
        assertTrue(books.get(0).isAvailable());
        assertEquals("1984", books.get(1).getTitle());
        assertFalse(books.get(1).isAvailable());
    }

    @Test
    void getInstance_returnsSameInstance() {
        FileBookRepository r1 = FileBookRepository.getInstance();
        FileBookRepository r2 = FileBookRepository.getInstance();
        assertSame(r1, r2);
    }

    @Test
    void saveBook_whenFileDoesNotExist_createsFile() throws Exception {
        Files.deleteIfExists(testFile);

        try (MockedStatic<FileMediaCopyRepository> mocked = mockStatic(FileMediaCopyRepository.class)) {
            mocked.when(FileMediaCopyRepository::getInstance).thenReturn(mock(FileMediaCopyRepository.class));

            FileBookRepository.saveBook(SAMPLE_BOOK, 1);

            assertTrue(Files.exists(testFile));
            assertTrue(Files.size(testFile) > 0);
        }
    }
}