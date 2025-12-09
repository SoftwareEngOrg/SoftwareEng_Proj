// src/test/java/Service/FileUserRepositoryTest.java

package Service;

import Domain.User;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileUserRepositoryTest {


    private Path tempUsersFile;
    private String originalRepoPath;
    private FileUserRepository repo;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeAll
    void beforeAll() throws IOException {
        originalRepoPath = FileUserRepository.repoPath;
        tempUsersFile = Files.createTempFile("users_repo_test_", ".txt");
        FileUserRepository.repoPath = tempUsersFile.toString();
        repo = new FileUserRepository();
    }

    @AfterAll
    void afterAll() throws IOException {
        // restore original repo path
        FileUserRepository.repoPath = originalRepoPath;
        Files.deleteIfExists(tempUsersFile);
    }

    @BeforeEach
    void setup() throws IOException {
        // prepare a known baseline file content before each test
        // format: username;password;role;email;date
        String now = dateFormat.format(new Date());
        String earlier = "2020-01-01";
        String content =
                "alice;pw1;customer;alice@test.com;" + earlier + "\n" +
                        "bob;pw2;customer;bob@test.com;" + now + "\n" +
                        "charlie;pw3;admin;charlie@test.com;" + earlier + "\n";
        Files.writeString(tempUsersFile, content, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Test
    void getAllUsers_readsAll() {
        List<User> users = repo.getAllUsers();
        assertEquals(3, users.size());
        Set<String> names = new HashSet<>();
        for (User u : users) names.add(u.getUsername());
        assertTrue(names.contains("alice"));
        assertTrue(names.contains("bob"));
        assertTrue(names.contains("charlie"));
    }

    @Test
    void findUser_validCredentials_returnsUser() {
        User u = repo.findUser("alice", "pw1");
        assertNotNull(u);
        assertEquals("alice", u.getUsername());
    }

    @Test
    void findUser_invalidPassword_returnsNull() {
        assertNull(repo.findUser("alice", "wrong"));
    }

    @Test
    void isUsernameExists_trueAndFalse() {
        assertTrue(repo.isUsernameExists("bob"));
        assertFalse(repo.isUsernameExists("nonexistent"));
    }

    @Test
    void addUser_appendsAndPreventsDuplicate() throws IOException {
        boolean added = repo.addUser("dave", "pw4", "dave@test.com", new Date());
        assertTrue(added);

        // verify appended
        String file = Files.readString(tempUsersFile);
        assertTrue(file.contains("dave;pw4;customer;dave@test.com"));

        // adding same username again should fail
        boolean addedAgain = repo.addUser("dave", "pw-other", "dtest", new Date());
        assertFalse(addedAgain);
    }

    @Test
    void updateDate_changesDateForUser() throws IOException {
        // read a user, call updateDate, then verify file date updated for that username
        User before = repo.findUser("alice", "pw1");
        assertNotNull(before);

        repo.updateDate(before); // method sets current date in file

        String file = Files.readString(tempUsersFile);
        String today = dateFormat.format(new Date());
        // ensure alice's line ends with today's date
        boolean aliceHasToday = Arrays.stream(file.split("\n"))
                .filter(l -> l.startsWith("alice;"))
                .anyMatch(l -> l.endsWith(today));
        assertTrue(aliceHasToday);
    }

    @Test
    void unregisterUserByUsername_removesUserAndReturnsTrue() throws IOException {
        boolean removed = repo.unregisterUserByUsername("charlie");
        assertTrue(removed);
        String file = Files.readString(tempUsersFile);
        assertFalse(file.contains("charlie;"));
    }

    @Test
    void unregisterUserByUsername_nonexistent_returnsFalse() {
        boolean removed = repo.unregisterUserByUsername("noone");
        assertFalse(removed);
    }

    @Test
    void unregisterAllUsers_removesProvidedList() throws IOException {
        // prepare a list containing alice and bob as inactive
        User u1 = new User("alice", "pw1", "customer", "alice@test.com", new Date());
        User u2 = new User("bob", "pw2", "customer", "bob@test.com", new Date());

        boolean result = repo.unregisterAllUsers(Arrays.asList(u1, u2));
        assertTrue(result);

        String file = Files.readString(tempUsersFile);
        assertFalse(file.contains("alice;"));
        assertFalse(file.contains("bob;"));
        // charlie should remain
        assertTrue(file.contains("charlie;"));
    }

    @Test
    void findUserByUsername_returnsUserOrNull() {
        User found = repo.findUserByUsername("bob");
        assertNotNull(found);
        assertEquals("bob", found.getUsername());

        assertNull(repo.findUserByUsername("doesnotexist"));
    }

    @Test
    void malformedLines_areIgnoredAndValidLinesParsed() throws IOException {
        // add a malformed line and a valid extra line
        String extra = "badlinewithoutsemicolons\n" +
                "eve;pw5;customer;eve@test.com;2022-02-02\n";
        Files.writeString(tempUsersFile, Files.readString(tempUsersFile) + extra, StandardOpenOption.TRUNCATE_EXISTING);

        List<User> users = repo.getAllUsers();
        // original 3 plus eve => 4 valid users; malformed line ignored
        assertTrue(users.stream().anyMatch(u -> "eve".equals(u.getUsername())));
        assertEquals(4, users.size());
    }
}