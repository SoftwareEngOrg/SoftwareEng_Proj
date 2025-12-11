package Service;

import Domain.Book;
import Domain.CD;
import Domain.MediaCopy;
import Domain.MediaItem;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("FileMediaCopyRepository - Additional Branch Coverage Tests")
class FileMediaCopyRepositoryTest {

    private Path tempCopiesFile;
    private Path tempBooksFile;
    private Path tempCDFile;
    private FileMediaCopyRepository repo;

    @BeforeAll
    void setUpTempFiles() throws IOException {
        tempCopiesFile = Files.createTempFile("test_copies_branch", ".txt");
        tempBooksFile = Files.createTempFile("test_books_branch", ".txt");
        tempCDFile = Files.createTempFile("test_cds_branch", ".txt");
    }

    @BeforeEach
    void setUp() throws IOException {
        FileMediaCopyRepository.reset();
        FileBookRepository.reset();
        FileCDRepository.reset();

        FileMediaCopyRepository.setRepoPath(tempCopiesFile.toString());
        FileBookRepository.setRepoPath(tempBooksFile.toString());
        FileCDRepository.repoPath = tempCDFile.toString();

        Files.writeString(tempCopiesFile, "");
        Files.writeString(tempBooksFile, "");
        Files.writeString(tempCDFile, "");

        repo = FileMediaCopyRepository.getInstance();
    }

    @AfterAll
    void cleanUp() throws IOException {
        Files.deleteIfExists(tempCopiesFile);
        Files.deleteIfExists(tempBooksFile);
        Files.deleteIfExists(tempCDFile);
    }

    // ========== Branch Coverage Tests ==========

    @Test
    @DisplayName("getMaxCopyIndexForIsbn - copyId without dash (parts.length = 1)")
    void getMaxCopyIndexForIsbn_NoDashInCopyId() throws Exception {
        // Create a book
        Book book = new Book("Test", "Author", "ISBN-001");
        FileBookRepository.saveBook(book, 0);

        // Manually add a copy with NO dash in copyId
        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        MediaCopy copy = new MediaCopy("NODASH", book);
        copy.setAvailable(true);

        // Access private field to add copy directly
        var field = repo.getClass().getDeclaredField("copies");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<MediaCopy> copies = (List<MediaCopy>) field.get(repo);
        copies.add(copy);

        // Call private method to test
        Method method = repo.getClass().getDeclaredMethod("getMaxCopyIndexForIsbn", String.class);
        method.setAccessible(true);
        int result = (int) method.invoke(repo, "ISBN-001");

        // Should return 0 because no valid index found
        assertEquals(0, result);
    }

    @Test
    @DisplayName("getMaxCopyIndexForIsbn - copyId with non-numeric suffix")
    void getMaxCopyIndexForIsbn_NonNumericSuffix() throws Exception {
        Book book = new Book("Test", "Author", "ISBN-002");
        FileBookRepository.saveBook(book, 0);

        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        // Copy with non-numeric suffix like "ISBN-002-ABC"
        MediaCopy copy = new MediaCopy("ISBN-002-ABC", book);

        var field = repo.getClass().getDeclaredField("copies");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<MediaCopy> copies = (List<MediaCopy>) field.get(repo);
        copies.add(copy);

        Method method = repo.getClass().getDeclaredMethod("getMaxCopyIndexForIsbn", String.class);
        method.setAccessible(true);
        int result = (int) method.invoke(repo, "ISBN-002");

        // Should return 0 because parsing fails
        assertEquals(0, result);
    }

    @Test
    @DisplayName("getMaxCopyIndexForIsbn - index not greater than max")
    void getMaxCopyIndexForIsbn_IndexNotGreaterThanMax() throws Exception {
        Book book = new Book("Test", "Author", "ISBN-003");
        FileBookRepository.saveBook(book, 3);

        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        // Add copies with indices: 5, 3, 2 (5 is max)
        MediaCopy copy1 = new MediaCopy("ISBN-003-5", book);
        MediaCopy copy2 = new MediaCopy("ISBN-003-3", book);
        MediaCopy copy3 = new MediaCopy("ISBN-003-2", book);

        var field = repo.getClass().getDeclaredField("copies");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<MediaCopy> copies = (List<MediaCopy>) field.get(repo);
        copies.clear();
        copies.add(copy1);
        copies.add(copy2);
        copies.add(copy3);

        Method method = repo.getClass().getDeclaredMethod("getMaxCopyIndexForIsbn", String.class);
        method.setAccessible(true);
        int result = (int) method.invoke(repo, "ISBN-003");

        // Should return 5 (the maximum)
        assertEquals(5, result);
    }

    @Test
    @DisplayName("addCopiesByBookIsbn - with zero copies")
    void addCopiesByBookIsbn_ZeroCopies() {
        Book book = new Book("Test", "Author", "ISBN-004");
        FileBookRepository.saveBook(book, 0);

        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        // Add zero copies (should return immediately)
        repo.addCopiesByBookIsbn("ISBN-004", 0, true);

        List<MediaCopy> copies = repo.getCopiesByIsbn("ISBN-004");
        assertEquals(0, copies.size());
    }

    @Test
    @DisplayName("addCopiesByBookIsbn - negative number of copies")
    void addCopiesByBookIsbn_NegativeCopies() {
        Book book = new Book("Test", "Author", "ISBN-005");
        FileBookRepository.saveBook(book, 0);

        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        // Try to add negative copies
        repo.addCopiesByBookIsbn("ISBN-005", -5, true);

        List<MediaCopy> copies = repo.getCopiesByIsbn("ISBN-005");
        assertEquals(0, copies.size());
    }

    @Test
    @DisplayName("addCopiesByBookIsbn - media item not found")
    void addCopiesByBookIsbn_MediaNotFound() {
        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        // Try to add copies for non-existent ISBN
        repo.addCopiesByBookIsbn("NON-EXISTENT", 5, true);

        List<MediaCopy> copies = repo.getCopiesByIsbn("NON-EXISTENT");
        assertEquals(0, copies.size());
    }

    @Test
    @DisplayName("getAvailableCopiesCount - with null MediaItem in copy")
    void getAvailableCopiesCount_NullMediaItem() throws Exception {
        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        // Create a copy with null MediaItem
        MediaCopy copyWithNull = new MediaCopy("COPY-NULL-1", null);
        copyWithNull.setAvailable(true);

        var field = repo.getClass().getDeclaredField("copies");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<MediaCopy> copies = (List<MediaCopy>) field.get(repo);
        copies.add(copyWithNull);

        // Should handle null gracefully (might throw NPE)
        assertThrows(NullPointerException.class, () -> {
            repo.getAvailableCopiesCount("ANY-ISBN");
        });
    }

    @Test
    @DisplayName("getCopiesByIsbn - with null MediaItem in copy")
    void getCopiesByIsbn_NullMediaItem() throws Exception {
        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        MediaCopy copyWithNull = new MediaCopy("COPY-NULL-2", null);

        var field = repo.getClass().getDeclaredField("copies");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<MediaCopy> copies = (List<MediaCopy>) field.get(repo);
        copies.add(copyWithNull);

        // Should handle null gracefully
        assertThrows(NullPointerException.class, () -> {
            repo.getCopiesByIsbn("ANY-ISBN");
        });
    }

    @Test
    @DisplayName("loadFromFile - with malformed line (less than 3 parts)")
    void loadFromFile_MalformedLine() throws IOException {
        Book book = new Book("Test", "Author", "ISBN-006");
        FileBookRepository.saveBook(book, 1);

        // Write malformed data
        Files.writeString(tempCopiesFile,
                "VALID-COPY;ISBN-006;true\n" +
                        "INVALID;ONLY-TWO\n" +  // Only 2 parts
                        "ANOTHER-VALID;ISBN-006;false\n"
        );

        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        List<MediaCopy> copies = repo.getCopiesByIsbn("ISBN-006");
        // Should only load the 2 valid copies
        assertEquals(2, copies.size());
    }

    @Test
    @DisplayName("loadFromFile - when file doesn't exist")
    void loadFromFile_FileNotExist() throws IOException {
        Files.delete(tempCopiesFile);

        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        // Should initialize with empty list
        List<MediaCopy> copies = repo.getCopiesByIsbn("ANY-ISBN");
        assertTrue(copies.isEmpty());
    }

    @Test
    @DisplayName("findMediaItem - returns CD when book not found")
    void findMediaItem_ReturnsCD() {
        CD cd = new CD("Album", "Artist", "CD-001");
        FileCDRepository.saveCD(cd, 1);

        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        MediaItem found = repo.findMediaItem("CD-001");

        assertNotNull(found);
        assertTrue(found instanceof CD);
        assertEquals("Album", found.getTitle());
    }

    @Test
    @DisplayName("findMediaItem - returns null when neither book nor CD found")
    void findMediaItem_ReturnsNull() {
        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        MediaItem found = repo.findMediaItem("NON-EXISTENT");

        assertNull(found);
    }

    @Test
    @DisplayName("saveToFile - handles empty copies list")
    void saveToFile_EmptyList() throws Exception {
        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        // Clear copies and save
        var field = repo.getClass().getDeclaredField("copies");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<MediaCopy> copies = (List<MediaCopy>) field.get(repo);
        copies.clear();

        repo.saveToFile();

        String content = Files.readString(tempCopiesFile);
        assertTrue(content.isEmpty() || content.trim().isEmpty());
    }

    @Test
    @DisplayName("getCopiesByIsbn - returns empty list when no matches")
    void getCopiesByIsbn_NoMatches() {
        Book book = new Book("Test", "Author", "ISBN-007");
        FileBookRepository.saveBook(book, 2);

        FileMediaCopyRepository.reset();
        repo = FileMediaCopyRepository.getInstance();

        // Add copies for ISBN-007
        repo.addCopiesByBookIsbn("ISBN-007", 2, true);

        // Search for different ISBN
        List<MediaCopy> copies = repo.getCopiesByIsbn("ISBN-999");

        assertTrue(copies.isEmpty());
    }

}
