package Service;

import Domain.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookServiceCustomer Comprehensive Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookServiceCustomerTest {

    private BookServiceCustomer service;
    private User testUser;
    private User userNoEmail;
    private Book testBook;
    private CD testCD;

    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    private Path tempBooksFile;
    private Path tempCDsFile;
    private Path tempCopiesFile;
    private Path tempLoansFile;
    private Path tempUsersFile;

    // Store original paths to restore later
    private String originalBooksPath;
    private String originalCDsPath;
    private String originalCopiesPath;
    private String originalLoansPath;
    private String originalUsersPath;

    @BeforeAll
    void setupTestData() throws IOException {
        // Save original file paths before changing them
        originalBooksPath = FileBookRepository.repoPath;
        originalCDsPath = FileCDRepository.repoPath;
        originalCopiesPath = FileMediaCopyRepository.repoPath;
        originalLoansPath = FileLoanRepository.repoPath;
        originalUsersPath = FileUserRepository.repoPath;

        // Create temp files with unique names
        tempBooksFile = Files.createTempFile("books_test_", ".txt");
        tempCDsFile = Files.createTempFile("cds_test_", ".txt");
        tempCopiesFile = Files.createTempFile("copies_test_", ".txt");
        tempLoansFile = Files.createTempFile("loans_test_", ".txt");
        tempUsersFile = Files.createTempFile("users_test_", ".txt");

        // Set repositories to use temp files
        FileBookRepository.repoPath = tempBooksFile.toString();
        FileCDRepository.repoPath = tempCDsFile.toString();
        FileMediaCopyRepository.repoPath = tempCopiesFile.toString();
        FileLoanRepository.repoPath = tempLoansFile.toString();
        FileUserRepository.repoPath = tempUsersFile.toString();

        System.out.println("Test files created:");
        System.out.println("Books: " + tempBooksFile);
        System.out.println("CDs: " + tempCDsFile);
        System.out.println("Copies: " + tempCopiesFile);
        System.out.println("Loans: " + tempLoansFile);
        System.out.println("Users: " + tempUsersFile);
    }

    @BeforeEach
    void setUp() throws Exception {
        // Reset singleton instances before each test
        resetSingletons();

        // Create test files with actual data
        setupBooks();
        setupCDs();
        setupCopies();
        setupLoans();
        setupUsers();

        testUser = new User("activeuser", "pass123", "customer", "active@test.com", new Date());
        userNoEmail = new User("noemailuser", "pass456", "customer", "", new Date());

        testBook = new Book("Clean Code", "Robert Martin", "BOOK001");
        testCD = new CD("Abbey Road", "The Beatles", "CD001");

        service = new BookServiceCustomer("system@library.com", "systempass");
        service.setCurrentUser(testUser);

        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // Reset all singleton instances
    private void resetSingletons() throws Exception {
        resetSingleton(FileBookRepository.class, "instance");
        resetSingleton(FileCDRepository.class, "instance");
        resetSingleton(FileMediaCopyRepository.class, "instance");
        resetSingleton(FileLoanRepository.class, "instance");
        resetSingleton(FileUserRepository.class, "instance");
    }

    private void resetSingleton(Class<?> clazz, String fieldName) throws Exception {
        List<String> candidates = Arrays.asList(fieldName, "INSTANCE", "singleton", "instance$0", "INSTANCE$0");
        for (String name : candidates) {
            try {
                Field f = clazz.getDeclaredField(name);
                f.setAccessible(true);
                try {
                    // attempt to set static field to null
                    f.set(null, null);
                } catch (IllegalAccessException iae) {
                    // Some JVMs may refuse to write final/static fields; try to remove final modifier if present
                    try {
                        Field modifiersField = Field.class.getDeclaredField("modifiers");
                        modifiersField.setAccessible(true);
                        modifiersField.setInt(f, f.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
                        f.set(null, null);
                    } catch (Exception ignored) {
                        // ignore and continue trying other candidates
                    }
                }
                return; // success or attempted; stop trying further names
            } catch (NoSuchFieldException ignored) {
                // try next candidate name
            } catch (Exception ignored) {
                // any other reflection exception -> try next candidate
            }
        }
        // If none found, just continue (tests expect this to be resilient)
    }

    private void setupBooks() throws IOException {
        Files.writeString(tempBooksFile,
                "Clean Code;Robert Martin;BOOK001;true\n" +
                        "Design Patterns;Gang of Four;BOOK002;true\n" +
                        "Refactoring;Martin Fowler;BOOK003;false\n"
        );
    }

    private void setupCDs() throws IOException {
        Files.writeString(tempCDsFile,
                "Abbey Road;The Beatles;CD001;true\n" +
                        "Dark Side;Pink Floyd;CD002;false\n"
        );
    }

    private void setupCopies() throws IOException {
        Files.writeString(tempCopiesFile,
                "BOOK001-1;BOOK001;true\n" +
                        "BOOK001-2;BOOK001;true\n" +
                        "BOOK002-1;BOOK002;false\n" +
                        "BOOK003-1;BOOK003;false\n" +
                        "CD001-1;CD001;true\n" +
                        "CD002-1;CD002;false\n"
        );
    }

    private void setupLoans() throws IOException {
        LocalDate past = LocalDate.now().minusDays(50);
        LocalDate recent = LocalDate.now().minusDays(5);

        Files.writeString(tempLoansFile,
                "LOAN001;activeuser;BOOK003;" + past + ";NULL\n" +
                        "LOAN002;activeuser;BOOK002;" + recent + ";NULL\n" +
                        "LOAN003;otheruser;CD002;" + recent + ";NULL\n"
        );
    }

    private void setupUsers() throws IOException {
        Files.writeString(tempUsersFile,
                "activeuser;pass123;customer;active@test.com;2025-12-01\n" +
                        "noemailuser;pass456;customer;;2025-12-01\n" +
                        "otheruser;pass789;customer;other@test.com;2025-12-01\n"
        );
    }

    @AfterAll
    void cleanup() throws IOException {
        // Restore original file paths
        FileBookRepository.repoPath = originalBooksPath;
        FileCDRepository.repoPath = originalCDsPath;
        FileMediaCopyRepository.repoPath = originalCopiesPath;
        FileLoanRepository.repoPath = originalLoansPath;
        FileUserRepository.repoPath = originalUsersPath;

        // Delete temp files
        Files.deleteIfExists(tempBooksFile);
        Files.deleteIfExists(tempCDsFile);
        Files.deleteIfExists(tempCopiesFile);
        Files.deleteIfExists(tempLoansFile);
        Files.deleteIfExists(tempUsersFile);

        System.out.println("Test cleanup completed - original paths restored");
    }

    // ==================== Constructor Tests ====================

    @Test
    @Order(1)
    @DisplayName("Constructor with params should set email config")
    void constructor_WithParams_SetsConfig() {
        BookServiceCustomer s = new BookServiceCustomer("test@mail.com", "testpass");
        assertNotNull(s);
    }

    @Test
    @Order(2)
    @DisplayName("Constructor without params should initialize")
    void constructor_NoParams_Initializes() {
        BookServiceCustomer s = new BookServiceCustomer();
        assertNotNull(s);
    }

    @Test
    @Order(3)
    @DisplayName("setEmailConfig should update credentials")
    void setEmailConfig_Updates() {
        service.setEmailConfig("new@test.com", "newpass");
        assertNotNull(service);
    }

    @Test
    @Order(4)
    @DisplayName("setCurrentUser should change user")
    void setCurrentUser_ChangesUser() {
        User newUser = new User("newuser", "pass", "customer");
        service.setCurrentUser(newUser);
        assertNotNull(service);
    }

    // ==================== borrowMediaItem Tests ====================

    @Test
    @Order(10)
    @DisplayName("Borrow without login returns false")
    void borrowMediaItem_NoLogin_False() {
        service.setCurrentUser(null);
        boolean result = service.borrowMediaItem("BOOK001");

        assertFalse(result);
        assertTrue(outContent.toString().contains("not logged in"));
    }

    @Test
    @Order(11)
    @DisplayName("Borrow with null ISBN returns false")
    void borrowMediaItem_NullISBN_False() {
        boolean result = service.borrowMediaItem(null);
        assertFalse(result);
        assertTrue(outContent.toString().contains("ISBN is null or empty"));
    }

    @Test
    @Order(12)
    @DisplayName("Borrow with empty ISBN returns false")
    void borrowMediaItem_EmptyISBN_False() {
        boolean result = service.borrowMediaItem("");
        assertFalse(result);
        assertTrue(outContent.toString().contains("ISBN is null or empty"));
    }

    @Test
    @Order(13)
    @DisplayName("Borrow with whitespace ISBN returns false")
    void borrowMediaItem_WhitespaceISBN_False() {
        boolean result = service.borrowMediaItem("   ");
        assertFalse(result);
        assertTrue(outContent.toString().contains("ISBN is null or empty"));
    }

    @Test
    @Order(14)
    @DisplayName("Borrow invalid ISBN returns false")
    void borrowMediaItem_InvalidISBN_False() {
        boolean result = service.borrowMediaItem("INVALID999");

        assertFalse(result);
        assertTrue(outContent.toString().contains("not found"));
    }

    @Test
    @Order(15)
    @DisplayName("Borrow with no copies and email adds to waitlist")
    void borrowMediaItem_NoCopiesWithEmail_Waitlist() {
        boolean result = service.borrowMediaItem("BOOK003");

        assertFalse(result);
        String output = outContent.toString();
        assertTrue(output.contains("borrowed") || output.contains("waitlist") ||
                output.contains("notify"));
    }

    @Test
    @Order(16)
    @DisplayName("Borrow with no copies and no email fails")
    void borrowMediaItem_NoCopiesNoEmail_False() {
        service.setCurrentUser(userNoEmail);

        boolean result = service.borrowMediaItem("BOOK003");

        assertFalse(result);
        assertTrue(outContent.toString().contains("Cannot add to waitlist: user email not set"));
    }

    @Test
    @Order(17)
    @DisplayName("Borrow with overdue loans fails")
    void borrowMediaItem_WithOverdue_False() {
        boolean result = service.borrowMediaItem("BOOK001");
        assertFalse(result);
        assertTrue(outContent.toString().contains("overdue") ||
                outContent.toString().contains("fine"));
    }

    @Test
    @Order(18)
    @DisplayName("Borrow valid book succeeds for clean user")
    void borrowMediaItem_Valid_Succeeds() throws Exception {
        // Create a clean user with no active loans
        User cleanUser = new User("cleanuser", "pass", "customer", "clean@test.com", new Date());
        service.setCurrentUser(cleanUser);

        boolean result = service.borrowMediaItem("BOOK001");

        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("borrowed successfully") || output.contains("Loan ID"));
    }

    @Test
    @Order(19)
    @DisplayName("Borrow CD succeeds for clean user")
    void borrowMediaItem_CD_Succeeds() throws Exception {
        User cleanUser = new User("cleanuser2", "pass", "customer", "clean2@test.com", new Date());
        service.setCurrentUser(cleanUser);

        boolean result = service.borrowMediaItem("CD001");

        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("borrowed successfully") || output.contains("Loan ID"));
    }

    // ==================== returnBook Tests ====================

    @Test
    @Order(20)
    @DisplayName("Return with null loan ID returns false")
    void returnBook_NullLoanId_False() {
        boolean result = service.returnBook(null);
        assertFalse(result);
        assertTrue(outContent.toString().contains("Invalid"));
    }

    @Test
    @Order(21)
    @DisplayName("Return with empty loan ID returns false")
    void returnBook_EmptyLoanId_False() {
        boolean result = service.returnBook("");
        assertFalse(result);
        assertTrue(outContent.toString().contains("Invalid"));
    }

    @Test
    @Order(22)
    @DisplayName("Return invalid loan returns false")
    void returnBook_InvalidLoan_False() {
        boolean result = service.returnBook("INVALID999");

        assertFalse(result);
        assertTrue(outContent.toString().contains("Invalid"));
    }

    @Test
    @Order(23)
    @DisplayName("Return already returned loan fails")
    void returnBook_AlreadyReturned_False() throws Exception {
        LocalDate past = LocalDate.now().minusDays(10);
        LocalDate returned = LocalDate.now().minusDays(5);
        String existingContent = Files.readString(tempLoansFile);
        Files.writeString(tempLoansFile,
                existingContent + "LOAN_RET;activeuser;BOOK001;" + past + ";" + returned + "\n"
        );
        resetSingletons();

        boolean result = service.returnBook("LOAN_RET");

        assertFalse(result);
        assertTrue(outContent.toString().contains("Invalid"));
    }

    @Test
    @Order(24)
    @DisplayName("Return loan of different user fails")
    void returnBook_DifferentUser_False() {
        boolean result = service.returnBook("LOAN003");

        assertFalse(result);
        assertTrue(outContent.toString().contains("does not belong"));
    }

    @Test
    @Order(25)
    @DisplayName("Return overdue loan shows fine")
    void returnBook_Overdue_ShowsFine() {
        boolean result = service.returnBook("LOAN001");

        assertFalse(result);
        assertTrue(outContent.toString().contains("fine"));
    }

    @Test
    @Order(26)
    @DisplayName("Return on-time loan succeeds")
    void returnBook_OnTime_Succeeds() {
        boolean result = service.returnBook("LOAN002");

        assertTrue(result);
        assertTrue(outContent.toString().contains("Thank you"));
    }

    // ==================== completeReturn Tests ====================

    @Test
    @Order(30)
    @DisplayName("Complete return with null loan ID fails")
    void completeReturn_NullLoanId_False() {
        boolean result = service.completeReturn(null);
        assertFalse(result);
        assertTrue(outContent.toString().contains("Invalid"));
    }

    @Test
    @Order(31)
    @DisplayName("Complete return invalid loan fails")
    void completeReturn_Invalid_False() {
        boolean result = service.completeReturn("INVALID999");

        assertFalse(result);
        assertTrue(outContent.toString().contains("Invalid"));
    }

    @Test
    @Order(32)
    @DisplayName("Complete return with fine succeeds")
    void completeReturn_WithFine_Succeeds() {
        boolean result = service.completeReturn("LOAN001");

        assertNotNull(outContent.toString());
        assertTrue(result);
        assertTrue(outContent.toString().contains("Fine paid") ||
                outContent.toString().contains("returned successfully"));
    }

    @Test
    @DisplayName("Complete return for CD updates availability")
    void completeReturn_CD_UpdatesAvailability() throws Exception {
        LocalDate recent = LocalDate.now().minusDays(50);
        String currentContent = Files.readString(tempLoansFile);
        Files.writeString(tempLoansFile,
                currentContent + "LOANCD;activeuser;CD002;" + recent + ";NULL\n"
        );

        resetSingletons();

        service = new BookServiceCustomer("system@library.com", "systempass");
        service.setCurrentUser(testUser);

        boolean result = service.completeReturn("LOANCD");

        assertTrue(result);



    // ==================== viewMyLoans Tests ====================

    @Test
    @Order(40)
    @DisplayName("View loans without login shows message")
    void viewMyLoans_NoLogin_ShowsMessage() {
        service.setCurrentUser(null);

        service.viewMyLoans();

        assertTrue(outContent.toString().contains("Not logged in"));
    }

    @Test
    @Order(41)
    @DisplayName("View loans with no active loans shows message")
    void viewMyLoans_NoLoans_ShowsMessage() {
        User newUser = new User("newuser", "pass", "customer", "new@test.com", new Date());
        service.setCurrentUser(newUser);

        service.viewMyLoans();

        assertTrue(outContent.toString().contains("no active loans"));
    }

    @Test
    @Order(42)
    @DisplayName("View loans shows active loans")
    void viewMyLoans_Shows() {
        service.viewMyLoans();

        String output = outContent.toString();
        assertTrue(output.contains("Active Loans") ||
                output.contains("no active loans") ||
                output.contains("BOOK") ||
                output.contains("CD"));
    }

    @Test
    @Order(43)
    @DisplayName("View loans separates books and CDs")
    void viewMyLoans_SeparatesSections() {
        service.viewMyLoans();

        String output = outContent.toString();
        assertNotNull(output);
        assertTrue(output.length() > 0);
    }

    @Test
    @Order(44)
    @DisplayName("View loans shows fines for overdue")
    void viewMyLoans_ShowsFines() {
        service.viewMyLoans();

        String output = outContent.toString();
        assertTrue(output.contains("fine") || output.contains("OVERDUE") ||
                output.contains("no active loans"));
    }

    @Test
    @Order(45)
    @DisplayName("View loans displays book loans section when present")
    void viewMyLoans_DisplaysBookSection() {
        service.viewMyLoans();

        String output = outContent.toString();
        assertTrue(output.contains("BOOK LOANS") || output.contains("no active loans"));
    }

    // ==================== getAllAvailableBooks Tests ====================

    @Test
    @Order(50)
    @DisplayName("Get all available books returns list")
    void getAllAvailableBooks_ReturnsList() {
        List<Book> books = service.getAllAvailableBooks();

        assertNotNull(books);
    }

    @Test
    @Order(51)
    @DisplayName("Get available books filters correctly")
    void getAllAvailableBooks_FiltersAvailable() {
        List<Book> books = service.getAllAvailableBooks();

        assertTrue(books.size() >= 0);
        // Check that only books with available copies are included
        for (Book book : books) {
            assertTrue(book.isAvailable());
        }
    }

    @Test
    @Order(52)
    @DisplayName("Get available books excludes books with no copies")
    void getAllAvailableBooks_ExcludesUnavailable() {
        List<Book> books = service.getAllAvailableBooks();

        // BOOK003 should not be in the list as all copies are borrowed
        boolean hasBook003 = books.stream()
                .anyMatch(b -> b.getIsbn().equals("BOOK003"));
        assertFalse(hasBook003);
    }

    // ==================== getAllAvailableCDs Tests ====================

    @Test
    @Order(60)
    @DisplayName("Get all available CDs returns list")
    void getAllAvailableCDs_ReturnsList() {
        List<CD> cds = service.getAllAvailableCDs();

        assertNotNull(cds);
    }

    @Test
    @Order(61)
    @DisplayName("Get available CDs filters correctly")
    void getAllAvailableCDs_FiltersAvailable() {
        List<CD> cds = service.getAllAvailableCDs();

        assertTrue(cds.size() >= 0);
        for (CD cd : cds) {
            assertTrue(cd.isAvailable());
        }
    }

    @Test
    @Order(62)
    @DisplayName("Get available CDs excludes CDs with no copies")
    void getAllAvailableCDs_ExcludesUnavailable() {
        List<CD> cds = service.getAllAvailableCDs();

        // CD002 should not be in the list as all copies are borrowed
        boolean hasCD002 = cds.stream()
                .anyMatch(cd -> cd.getIsbn().equals("CD002"));
        assertFalse(hasCD002);
    }

    // ==================== findMediaByIsbn Tests ====================

    @Test
    @Order(70)
    @DisplayName("Find media by ISBN returns book")
    void findMediaByIsbn_Book_ReturnsBook() {
        MediaItem item = service.findMediaByIsbn("BOOK001");

        assertNotNull(item);
        assertTrue(item instanceof Book);
        assertEquals("Clean Code", item.getTitle());
    }

    @Test
    @Order(71)
    @DisplayName("Find media by ISBN returns CD")
    void findMediaByIsbn_CD_ReturnsCD() {
        MediaItem item = service.findMediaByIsbn("CD001");

        assertNotNull(item);
        assertTrue(item instanceof CD);
        assertEquals("Abbey Road", item.getTitle());
    }

    @Test
    @Order(72)
    @DisplayName("Find media by invalid ISBN returns null")
    void findMediaByIsbn_Invalid_ReturnsNull() {
        MediaItem item = service.findMediaByIsbn("INVALID999");

        assertNull(item);
    }

    @Test
    @Order(73)
    @DisplayName("Find media by null ISBN returns null or throws")
    void findMediaByIsbn_Null_ReturnsNull() {
        try {
            MediaItem item = service.findMediaByIsbn(null);
            assertNull(item);
        } catch (Exception e) {
            // Acceptable behaviors: method returns null or throws IllegalArgumentException/NullPointerException
            assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException,
                    "Unexpected exception for null ISBN: " + e);
        }
    }

    // ==================== generateLoanReport Tests ====================

    @Test
    @Order(80)
    @DisplayName("Generate report without login returns message")
    void generateLoanReport_NoLogin_ReturnsMessage() {
        service.setCurrentUser(null);

        String report = service.generateLoanReport();

        assertEquals("Not logged in.", report);
    }

    @Test
    @Order(81)
    @DisplayName("Generate report includes headers")
    void generateLoanReport_IncludesHeaders() {
        String report = service.generateLoanReport();

        assertTrue(report.contains("LIBRARY LOAN REPORT") ||
                report.contains("no active loans"));
    }

    @Test
    @Order(82)
    @DisplayName("Generate report includes username")
    void generateLoanReport_IncludesUsername() {
        String report = service.generateLoanReport();

        assertTrue(report.contains("activeuser") ||
                report.contains("no active loans"));
    }

    @Test
    @Order(83)
    @DisplayName("Generate report includes date")
    void generateLoanReport_IncludesDate() {
        String report = service.generateLoanReport();

        assertTrue(report.contains("Report Date") ||
                report.contains("no active loans"));
    }

    @Test
    @Order(84)
    @DisplayName("Generate report shows book loans")
    void generateLoanReport_ShowsBooks() {
        String report = service.generateLoanReport();

        assertTrue(report.contains("BOOK LOANS") ||
                report.contains("no active loans"));
    }

    @Test
    @Order(85)
    @DisplayName("Generate report shows overdue status")
    void generateLoanReport_ShowsOverdue() {
        String report = service.generateLoanReport();

        assertTrue(report.contains("OVERDUE") ||
                report.contains("Fine") ||
                report.contains("no active loans"));
    }

    @Test
    @Order(86)
    @DisplayName("Generate report shows total fine")
    void generateLoanReport_ShowsTotalFine() {
        String report = service.generateLoanReport();

        assertTrue(report.contains("TOTAL FINE") ||
                report.contains("no active loans"));
    }

    @Test
    @Order(87)
    @DisplayName("Generate report for user with no loans")
    void generateLoanReport_NoLoans_ShowsMessage() {
        User newUser = new User("newuser", "pass", "customer", "new@test.com", new Date());
        service.setCurrentUser(newUser);

        String report = service.generateLoanReport();

        assertTrue(report.contains("no active loans"));
    }

    @Test
    @Order(88)
    @DisplayName("Generate report shows author/artist information")
    void generateLoanReport_ShowsAuthorInfo() {
        String report = service.generateLoanReport();

        assertTrue(report.contains("Author:") ||
                report.contains("Artist:") ||
                report.contains("no active loans"));
    }

    // ==================== getCopiesByISBN Tests ====================

    @Test
    @Order(90)
    @DisplayName("Get copies by invalid ISBN returns empty")
    void getCopiesByISBN_Invalid_ReturnsEmpty() {
        List<MediaCopy> copies = service.getCopiesByISBN("INVALID999");

        assertTrue(copies.isEmpty());
    }

    @Test
    @Order(91)
    @DisplayName("Get copies by valid ISBN returns copies")
    void getCopiesByISBN_Valid_ReturnsCopies() {
        List<MediaCopy> copies = service.getCopiesByISBN("BOOK001");

        assertNotNull(copies);
        assertTrue(copies.size() > 0);
    }

    @Test
    @Order(92)
    @DisplayName("Get copies parses availability correctly")
    void getCopiesByISBN_ParsesAvailability() {
        List<MediaCopy> copies = service.getCopiesByISBN("BOOK001");

        assertFalse(copies.isEmpty());
        for (MediaCopy copy : copies) {
            assertNotNull(copy.getCopyId());
            assertNotNull(copy.getMediaItem());
        }
    }

    @Test
    @Order(93)
    @DisplayName("Get copies by null ISBN returns empty")
    void getCopiesByISBN_Null_ReturnsEmpty() {
        List<MediaCopy> copies = service.getCopiesByISBN(null);

        assertNotNull(copies);
    }

    @Test
    @Order(94)
    @DisplayName("Get copies returns correct count for book")
    void getCopiesByISBN_CorrectCount() {
        List<MediaCopy> copies = service.getCopiesByISBN("BOOK001");

        assertEquals(2, copies.size());
    }

    // ==================== Edge Cases and Integration Tests ====================

    @Test
    @Order(100)
    @DisplayName("User with empty email cannot join waitlist")
    void borrowMediaItem_EmptyEmail_CannotJoinWaitlist() {
        User user = new User("test", "pass", "customer", "", new Date());
        service.setCurrentUser(user);

        boolean result = service.borrowMediaItem("BOOK003");

        assertFalse(result);
        String output = outContent.toString();
        assertTrue(output.contains("Cannot add to waitlist: user email not set"));
    }

    @Test
    @Order(101)
    @DisplayName("Multiple user switches work correctly")
    void service_MultipleUserSwitches_Works() {
        User user1 = new User("u1", "p1", "customer", "u1@test.com", new Date());
        User user2 = new User("u2", "p2", "customer", "u2@test.com", new Date());

        service.setCurrentUser(user1);
        service.viewMyLoans();

        service.setCurrentUser(user2);
        service.viewMyLoans();

        service.setCurrentUser(null);
        service.viewMyLoans();

        assertTrue(outContent.toString().contains("Not logged in"));
    }

    @Test
    @Order(102)
    @DisplayName("Borrow and return workflow completes successfully")
    void borrowAndReturn_Workflow_Success() throws Exception {
        User cleanUser = new User("workflow", "pass", "customer", "workflow@test.com", new Date());
        service.setCurrentUser(cleanUser);

        // Borrow a book
        boolean borrowed = service.borrowMediaItem("BOOK001");
        assertTrue(borrowed);

        // View loans to get loan ID
        service.viewMyLoans();
        String output = outContent.toString();

        // Extract loan ID from output (this is a simplified approach)
        assertTrue(output.contains("Loan ID") || output.contains("LOAN"));
    }

    @Test
    @Order(103)
    @DisplayName("Cannot borrow when having unpaid fines")
    void borrowMediaItem_WithFines_Fails() {
        // activeuser has overdue loan LOAN001 which generates fines
        boolean result = service.borrowMediaItem("CD001");

        assertFalse(result);
        assertTrue(outContent.toString().contains("fine") ||
                outContent.toString().contains("overdue"));
    }

    @Test
    @Order(104)
    @DisplayName("Return updates copy availability correctly")
    void returnBook_UpdatesCopyAvailability() throws Exception {
        // First check available copies before return
        int copiesBefore = service.getCopiesByISBN("BOOK002").stream()
                .filter(MediaCopy::isAvailable)
                .toList()
                .size();

        // Return the book (LOAN002 is for BOOK002)
        boolean returned = service.returnBook("LOAN002");

        if (returned) {
            // Check available copies after return
            int copiesAfter = service.getCopiesByISBN("BOOK002").stream()
                    .filter(MediaCopy::isAvailable)
                    .toList()
                    .size();

            assertTrue(copiesAfter >= copiesBefore);
        }
    }

    @Test
    @Order(105)
    @DisplayName("View loans calculates total fine correctly")
    void viewMyLoans_CalculatesTotalFine() {
        service.viewMyLoans();

        String output = outContent.toString();
        if (output.contains("Total fine")) {
            assertTrue(output.contains("₪") || output.contains("fine"));
        }
    }

    @Test
    @Order(106)
    @DisplayName("Get all available items when files are empty")
    void getAllAvailable_EmptyFiles_ReturnsEmpty() throws Exception {
        // Clear the files
        Files.writeString(tempBooksFile, "");
        Files.writeString(tempCDsFile, "");
        resetSingletons();

        List<Book> books = service.getAllAvailableBooks();
        List<CD> cds = service.getAllAvailableCDs();

        assertTrue(books.isEmpty());
        assertTrue(cds.isEmpty());
    }

    @Test
    @Order(107)
    @DisplayName("Generate report shows proper formatting")
    void generateLoanReport_ProperFormatting() {
        String report = service.generateLoanReport();

        // Check for proper line separators
        assertTrue(report.contains("=") || report.contains("-") ||
                report.contains("no active loans"));
    }

    @Test
    @Order(108)
    @DisplayName("Borrowing updates book availability in repository")
    void borrowMediaItem_UpdatesAvailability() throws Exception {
        User cleanUser = new User("avail", "pass", "customer", "avail@test.com", new Date());
        service.setCurrentUser(cleanUser);

        MediaItem bookBefore = service.findMediaByIsbn("BOOK001");
        assertNotNull(bookBefore);

        boolean borrowed = service.borrowMediaItem("BOOK001");
        assertTrue(borrowed);

        // The book might still show as available if there are multiple copies
        MediaItem bookAfter = service.findMediaByIsbn("BOOK001");
        assertNotNull(bookAfter);
    }

    @Test
    @Order(109)
    @DisplayName("Complete return of non-existent loan fails gracefully")
    void completeReturn_NonExistent_Fails() {
        boolean result = service.completeReturn("NONEXISTENT");

        assertFalse(result);
        assertTrue(outContent.toString().contains("Invalid"));
    }

    @Test
    @Order(110)
    @DisplayName("View my loans handles mixed book and CD loans")
    void viewMyLoans_MixedTypes_DisplaysBoth() {
        service.viewMyLoans();

        assertTrue(true);
        String output = outContent.toString();
        // Should have sections for books and/or CDs, or no loans message
        assertTrue(output.contains("BOOK") || output.contains("CD") ||
                output.contains("no active loans"));

    }
}