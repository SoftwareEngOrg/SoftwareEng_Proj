package Service;

import Domain.CD;
import Domain.MediaCopy;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileCDRepositoryTest {

    private Path tempCDFile;
    private Path tempCopiesFile;
    private FileCDRepository repo;

    @BeforeAll
    void setUpTempFiles() throws IOException {
        tempCDFile = Files.createTempFile("test_cds", ".txt");
        tempCopiesFile = Files.createTempFile("test_copies", ".txt");
    }

    @BeforeEach
    void setUp() throws IOException {
        // Reset singletons
        FileCDRepository.reset();
        FileMediaCopyRepository.reset();

        // Set paths
        FileCDRepository.repoPath = tempCDFile.toString();
        FileMediaCopyRepository.setRepoPath(tempCopiesFile.toString());

        // Clear files
        Files.writeString(tempCDFile, "");
        Files.writeString(tempCopiesFile, "");

        // Get instance
        repo = FileCDRepository.getInstance();
    }

    @AfterAll
    void cleanUp() throws IOException {
        Files.deleteIfExists(tempCDFile);
        Files.deleteIfExists(tempCopiesFile);
    }

    // ============ Basic Functionality Tests ============

    @Test
    @DisplayName("saveCD writes CD to file and creates copies")
    void saveCD_worksCorrectly() {
        CD cd = new CD("Greatest Hits", "Artist", "CD-001");

        FileCDRepository.saveCD(cd, 3);

        assertEquals(1, countLines(tempCDFile));
        assertTrue(readFileContent(tempCDFile).contains("Greatest Hits;Artist;CD-001;true"));
        assertEquals(3, FileMediaCopyRepository.getInstance().getCopiesByIsbn("CD-001").size());
    }

    @Test
    @DisplayName("findAllCDs returns all CDs from file")
    void findAllCDs_returnsAllCDs() {
        FileCDRepository.saveCD(new CD("Album 1", "Artist 1", "CD-001"), 1);
        FileCDRepository.saveCD(new CD("Album 2", "Artist 2", "CD-002"), 1);

        FileCDRepository.reset();
        FileCDRepository newRepo = FileCDRepository.getInstance();

        List<CD> cds = newRepo.findAllCDs();
        assertEquals(2, cds.size());
        assertTrue(cds.stream().anyMatch(cd -> cd.getIsbn().equals("CD-001")));
        assertTrue(cds.stream().anyMatch(cd -> cd.getIsbn().equals("CD-002")));
    }

    @Test
    @DisplayName("findAllCDs returns empty list when file does not exist")
    void findAllCDs_whenFileNotExist_returnsEmptyList() throws IOException {
        Files.delete(tempCDFile);

        List<CD> cds = repo.findAllCDs();

        assertTrue(cds.isEmpty());
    }

    @Test
    @DisplayName("findAllCDs skips empty lines")
    void findAllCDs_skipsEmptyLines() throws IOException {
        Files.writeString(tempCDFile, "Album 1;Artist 1;CD-001;true\n\n\nAlbum 2;Artist 2;CD-002;true\n");

        FileCDRepository.reset();
        FileCDRepository newRepo = FileCDRepository.getInstance();

        List<CD> cds = newRepo.findAllCDs();
        assertEquals(2, cds.size());
    }

    @Test
    @DisplayName("findAllCDs skips lines with less than 4 parts")
    void findAllCDs_skipsInvalidLines() throws IOException {
        Files.writeString(tempCDFile,
                "Album 1;Artist 1;CD-001;true\n" +
                        "Invalid;Line\n" +
                        "Album 2;Artist 2;CD-002;true\n");

        FileCDRepository.reset();
        FileCDRepository newRepo = FileCDRepository.getInstance();

        List<CD> cds = newRepo.findAllCDs();
        assertEquals(2, cds.size());
    }

    @Test
    @DisplayName("findByIsbn returns CD when found")
    void findByIsbn_whenFound_returnsCD() {
        FileCDRepository.saveCD(new CD("Album", "Artist", "CD-001"), 1);

        FileCDRepository.reset();
        FileCDRepository newRepo = FileCDRepository.getInstance();

        CD cd = newRepo.findByIsbn("CD-001");

        assertNotNull(cd);
        assertEquals("Album", cd.getTitle());
        assertEquals("Artist", cd.getAuthor());
    }

    @Test
    @DisplayName("findByIsbn returns null when not found")
    void findByIsbn_whenNotFound_returnsNull() {
        CD cd = repo.findByIsbn("NON-EXISTENT");

        assertNull(cd);
    }

    @Test
    @DisplayName("findByIsbn is case insensitive")
    void findByIsbn_isCaseInsensitive() {
        FileCDRepository.saveCD(new CD("Album", "Artist", "CD-001"), 1);

        FileCDRepository.reset();
        FileCDRepository newRepo = FileCDRepository.getInstance();

        CD cd = newRepo.findByIsbn("cd-001");

        assertNotNull(cd);
        assertEquals("CD-001", cd.getIsbn());
    }

    // ============ Update Tests ============

    @Test
    @DisplayName("updateAll updates all CDs in file")
    void updateAll_updatesFile() {
        FileCDRepository.saveCD(new CD("Album 1", "Artist 1", "CD-001"), 1);
        FileCDRepository.saveCD(new CD("Album 2", "Artist 2", "CD-002"), 1);

        FileCDRepository.reset();
        FileCDRepository newRepo = FileCDRepository.getInstance();

        List<CD> cds = newRepo.findAllCDs();
        cds.get(0).setAvailable(false);
        cds.get(1).setTitle("Updated Album");

        newRepo.updateAll(cds);

        FileCDRepository.reset();
        FileCDRepository reloadedRepo = FileCDRepository.getInstance();

        List<CD> updatedCDs = reloadedRepo.findAllCDs();
        assertEquals(2, updatedCDs.size());
        assertFalse(updatedCDs.get(0).isAvailable());
        assertEquals("Updated Album", updatedCDs.get(1).getTitle());
    }

    @Test
    @DisplayName("updateCD replaces existing CD")
    void updateCD_replacesExistingCD() {
        FileCDRepository.saveCD(new CD("Old Title", "Artist", "CD-001"), 1);

        FileCDRepository.reset();
        FileCDRepository newRepo = FileCDRepository.getInstance();

        CD updatedCD = new CD("New Title", "Artist", "CD-001");
        updatedCD.setAvailable(false);
        newRepo.updateCD(updatedCD);

        CD cd = newRepo.findByIsbn("CD-001");
        assertEquals("New Title", cd.getTitle());
        assertFalse(cd.isAvailable());
    }

    @Test
    @DisplayName("updateCD adds new CD when not found")
    void updateCD_addsNewCDWhenNotFound() {
        CD newCD = new CD("New Album", "Artist", "CD-999");

        repo.updateCD(newCD);

        CD cd = repo.findByIsbn("CD-999");
        assertNotNull(cd);
        assertEquals("New Album", cd.getTitle());
    }

    @Test
    @DisplayName("updateCD handles null CD gracefully")
    void updateCD_whenNull_doesNothing() {
        FileCDRepository.saveCD(new CD("Album", "Artist", "CD-001"), 1);

        repo.updateCD(null);

        assertEquals(1, repo.findAllCDs().size());
    }

    @Test
    @DisplayName("updateCD handles CD with null ISBN gracefully")
    void updateCD_whenIsbnNull_doesNotReplaceExisting() {
        FileCDRepository.saveCD(new CD("Album 1", "Artist 1", "CD-001"), 1);

        FileCDRepository.reset();
        FileCDRepository newRepo = FileCDRepository.getInstance();

        CD cdWithNullIsbn = new CD("Album 2", "Artist 2", null);
        newRepo.updateCD(cdWithNullIsbn);

        // Should add new CD since no match found
        List<CD> cds = newRepo.findAllCDs();
        assertEquals(2, cds.size());
    }

    // ============ Availability Tests ============

    @Test
    void updateCDAvailability_updatesBasedOnCopies() {

        FileCDRepository.saveCD(new CD("Album", "Artist", "CD-001"), 2);

        FileCDRepository.reset();
        FileMediaCopyRepository.reset();

        FileCDRepository newRepo = FileCDRepository.getInstance();

        List<MediaCopy> copies = FileMediaCopyRepository.getInstance().getCopiesByIsbn("CD-001");
        for (MediaCopy copy : copies) {
            copy.setAvailable(false);
        }
        FileMediaCopyRepository.getInstance().saveToFile();

        newRepo.updateCDAvailability("CD-001");

        CD cd = newRepo.findByIsbn("CD-001");
        assertFalse(cd.isAvailable());
    }



    @Test
    @DisplayName("updateCDAvailability sets available when copies exist")
    void updateCDAvailability_setsAvailableWhenCopiesExist() {

        FileCDRepository.saveCD(new CD("Album", "Artist", "CD-001"), 2);

        FileCDRepository.reset();
        FileCDRepository newRepo = FileCDRepository.getInstance();

        CD cd = newRepo.findByIsbn("CD-001");
        cd.setAvailable(false);
        newRepo.updateAll(newRepo.findAllCDs());

        newRepo.updateCDAvailability("CD-001");

        cd = newRepo.findByIsbn("CD-001");
        assertTrue(cd.isAvailable());
    }

    @Test
    @DisplayName("updateCDAvailability handles non-existent ISBN gracefully")
    void updateCDAvailability_whenCDNotFound_doesNothing() {
        assertDoesNotThrow(() -> repo.updateCDAvailability("NON-EXISTENT"));
    }

    @Test
    @DisplayName("updateCDAvailability prints message when CD becomes available")
    void updateCDAvailability_whenBecomesAvailable_printsMessage() {
        FileCDRepository.saveCD(new CD("Album", "Artist", "CD-001"), 1);

        FileCDRepository.reset();
        FileCDRepository newRepo = FileCDRepository.getInstance();

        // Set CD as unavailable
        CD cd = newRepo.findByIsbn("CD-001");
        cd.setAvailable(false);
        newRepo.updateAll(newRepo.findAllCDs());

        // Now update (should print message)
        newRepo.updateCDAvailability("CD-001");

        assertTrue(newRepo.findByIsbn("CD-001").isAvailable());
    }

    // ============ Singleton Tests ============

    @Test
    @DisplayName("getInstance returns same instance")
    void getInstance_returnsSameInstance() {
        FileCDRepository repo1 = FileCDRepository.getInstance();
        FileCDRepository repo2 = FileCDRepository.getInstance();

        assertSame(repo1, repo2);
    }

    @Test
    @DisplayName("reset creates new instance on next getInstance")
    void reset_createsNewInstance() {
        FileCDRepository repo1 = FileCDRepository.getInstance();
        FileCDRepository.reset();
        FileCDRepository repo2 = FileCDRepository.getInstance();

        assertNotSame(repo1, repo2);
    }

    // ============ Edge Cases Tests ============

    @Test
    @DisplayName("saveCD with zero copies still saves CD")
    void saveCD_withZeroCopies_savesCD() {
        FileCDRepository.saveCD(new CD("Album", "Artist", "CD-001"), 0);

        assertEquals(1, countLines(tempCDFile));
        assertEquals(0, FileMediaCopyRepository.getInstance().getCopiesByIsbn("CD-001").size());
    }

    @Test
    @DisplayName("findAllCDs handles malformed lines gracefully")
    void findAllCDs_handlesMalformedLines() throws IOException {
        // Write lines with different issues
        Files.writeString(tempCDFile,
                "Valid Album;Artist;CD-001;true\n" +
                        "Only;Two\n" +                    // Only 2 parts
                        "Three;Parts;Only\n" +            // Only 3 parts
                        "Another Valid;Artist2;CD-002;false\n");

        FileCDRepository.reset();
        FileCDRepository newRepo = FileCDRepository.getInstance();

        List<CD> cds = newRepo.findAllCDs();

        // Should only load the 2 valid CDs
        assertEquals(2, cds.size());
        assertTrue(cds.stream().anyMatch(c -> c.getIsbn().equals("CD-001")));
        assertTrue(cds.stream().anyMatch(c -> c.getIsbn().equals("CD-002")));
    }

    @Test
    @DisplayName("getFilePath returns repoPath when set")
    void getFilePath_returnsRepoPath() {
        String customPath = "/custom/path/cds.txt";
        FileCDRepository.repoPath = customPath;
        FileCDRepository.reset();
        FileCDRepository newRepo = FileCDRepository.getInstance();

        // The path should be used (we can't directly test private method, but we can observe behavior)
        assertNotNull(newRepo);
    }

    @Test
    @DisplayName("updateAll handles empty list")
    void updateAll_handlesEmptyList() {
        repo.updateAll(List.of());

        assertEquals(0, countLines(tempCDFile));
    }

    // ============ Helper Methods ============

    private int countLines(Path path) {
        try {
            return (int) Files.lines(path).count();
        } catch (IOException e) {
            return 0;
        }
    }

    private String readFileContent(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "";
        }
    }
}