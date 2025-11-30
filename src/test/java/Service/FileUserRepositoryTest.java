// src/test/java/Service/FileUserRepositoryTest.java

package Service;

import Domain.User;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileUserRepositoryTest {

    private FileUserRepository repo;
    private Path testFilePath;

    @BeforeAll
    void setUpTestFile() throws IOException {
        // Create a temporary file named "users_test.txt"
        testFilePath = Files.createTempFile("users_test", ".txt");
        FileUserRepository.repoPath = testFilePath.toString(); // Override static path
    }

    @BeforeEach
    void initRepositoryAndClearFile() throws IOException {
        // Clear the file content before each test
        Files.writeString(testFilePath, "");
        repo = new FileUserRepository();
    }

    @AfterAll
    void cleanup() throws IOException {
        Files.deleteIfExists(testFilePath);
    }

    @Test
    @DisplayName("findUser returns correct user when credentials match")
    void findUser_validCredentials_returnsUser() throws IOException {
        // Prepare test data in users_test.txt
        Files.writeString(testFilePath, """
            alice;secret123;customer
            bob;pass456;admin
            john;lib789;librarian
            """);

        User found = repo.findUser("bob", "pass456");

        assertNotNull(found);
        assertEquals("bob", found.getUsername());
        assertNotEquals("secret123", found.getPassword());  // password is stored
        assertEquals("admin", found.getRole());
    }

    @Test
    @DisplayName("findUser returns null when username is wrong")
    void findUser_wrongUsername_returnsNull() throws IOException {
        Files.writeString(testFilePath, "alice;123;customer");

        assertNull(repo.findUser("wronguser", "123"));
    }

    @Test
    @DisplayName("findUser returns null when password is wrong")
    void findUser_wrongPassword_returnsNull() throws IOException {
        Files.writeString(testFilePath, "alice;correctpass;customer");

        assertNull(repo.findUser("alice", "wrongpass"));
    }

    @Test
    @DisplayName("findUser returns null when file is empty")
    void findUser_emptyFile_returnsNull() {
        assertNull(repo.findUser("anyone", "anypass"));
    }

    @Test
    @DisplayName("findUser handles malformed lines (less than 3 parts)")
    void findUser_malformedLine_isSkipped() throws IOException {
        Files.writeString(testFilePath, """
            alice;pass  // only 2 parts → invalid
            bob;pass;admin
            """);

        User user = repo.findUser("bob", "pass");
        assertNotNull(user);
        assertEquals("admin", user.getRole());

        assertNull(repo.findUser("alice", "pass")); // malformed line → ignored
    }

    @Test
    @DisplayName("findUser handles missing file gracefully")
    void findUser_missingFile_returnsNull_andPrintsError() throws IOException {
        // Delete the file
        Files.deleteIfExists(testFilePath);

        // Capture System.out to verify error message
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        try {
            assertNull(repo.findUser("any", "any"));
            String output = outContent.toString();
            assertTrue(output.contains("Error reading users file."));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("findUser works with exact match only (case sensitive)")
    void findUser_caseSensitive_matching() throws IOException {
        Files.writeString(testFilePath, "Alice;Pass123;customer");

        assertNotNull(repo.findUser("Alice", "Pass123"));
        assertNull(repo.findUser("alice", "Pass123"));  // lowercase → no match
        assertNull(repo.findUser("Alice", "pass123"));  // wrong case in password
    }
}