package Service;

import Domain.Book;
import Domain.CD;
import Domain.Loan;
import Domain.MediaItem;
import Domain.User;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileLoanRepositoryTest {

    private Path tempLoansFile;
    private Path tempBooksFile;
    private Path tempCopiesFile;
    private Path tempCDFile;
    private FileLoanRepository repo;
    private User alice;
    private Book book;

    @BeforeAll
    void setUpTempFiles() throws IOException {
        tempLoansFile = Files.createTempFile("test_loans", ".txt");
        tempBooksFile = Files.createTempFile("test_books", ".txt");
        tempCopiesFile = Files.createTempFile("test_copies", ".txt");
        tempCDFile = Files.createTempFile("test_cds", ".txt");
    }

    @BeforeEach
    void setUp() throws IOException {
        // Reset all singletons
        FileBookRepository.reset();
        FileLoanRepository.reset();
        FileMediaCopyRepository.reset();
        FileCDRepository.reset();

        // Set paths
        FileBookRepository.setRepoPath(tempBooksFile.toString());
        FileLoanRepository.setRepoPath(tempLoansFile.toString());
        FileMediaCopyRepository.setRepoPath(tempCopiesFile.toString());
        FileCDRepository.repoPath = tempCDFile.toString();

        // Clear files
        Files.writeString(tempBooksFile, "");
        Files.writeString(tempLoansFile, "");
        Files.writeString(tempCopiesFile, "");
        Files.writeString(tempCDFile, "");

        // Setup test data
        alice = new User("alice", "123", "customer");
        book = new Book("Java", "Yahya", "999");
        book.setAvailable(true);

        FileBookRepository.getInstance().saveBook(book, 1);

        repo = new FileLoanRepository();
    }

    @AfterAll
    void cleanUp() throws IOException {
        Files.deleteIfExists(tempLoansFile);
        Files.deleteIfExists(tempBooksFile);
        Files.deleteIfExists(tempCopiesFile);
        Files.deleteIfExists(tempCDFile);
    }

    // ============ Basic Functionality Tests ============

    @Test
    @DisplayName("borrowItem creates loan and saves to file")
    void borrowItem_worksCorrectly() {
        Loan loan = repo.borrowItem(alice, book);

        assertNotNull(loan.getLoanId());
        assertEquals("alice", loan.getUser().getUsername());
        assertEquals("Java", loan.getMediaItem().getTitle());
        assertEquals(LocalDate.now(), loan.getBorrowDate());


        assertEquals(1, repo.getAllActiveLoans().size());
        assertEquals(1, countLines(tempLoansFile));
    }

    @Test
    @DisplayName("returnItem sets return date, makes book available, and saves to file")
    void returnItem_worksCorrectly() {
        Loan loan = repo.borrowItem(alice, book);
        LocalDate returnDate = LocalDate.now().plusDays(5);

        boolean result = repo.returnItem(loan.getLoanId(), returnDate);

        assertTrue(result);
        assertEquals(returnDate, loan.getReturnDate());
        assertTrue(book.isAvailable());
        assertEquals(0, repo.getAllActiveLoans().size());
        assertTrue(readFirstLine(tempLoansFile).endsWith(returnDate.toString()));
    }

    @Test
    @DisplayName("getActiveLoansForUser returns only active loans of the user")
    void getActiveLoansForUser_worksCorrectly() {
        repo.borrowItem(alice, book);

        User bob = new User("bob", "456", "customer");
        Book book2 = new Book("Python", "B", "888");
        FileBookRepository.getInstance().saveBook(book2, 1);
        repo = new FileLoanRepository();
        repo.borrowItem(bob, book2);

        List<Loan> aliceLoans = repo.getActiveLoansForUser("alice");
        assertEquals(1, aliceLoans.size());
        assertEquals("alice", aliceLoans.get(0).getUser().getUsername());
    }

    @Test
    @DisplayName("getAllActiveLoans returns only active loans")
    void getAllActiveLoans_excludesReturnedLoans() {
        Loan loan = repo.borrowItem(alice, book);
        repo.returnItem(loan.getLoanId(), LocalDate.now());

        assertEquals(0, repo.getAllActiveLoans().size());
    }

    @Test
    @DisplayName("findLoanById returns correct loan or null")
    void findLoanById_worksCorrectly() {
        Loan loan = repo.borrowItem(alice, book);

        assertEquals(loan, repo.findLoanById(loan.getLoanId()));
        assertNull(repo.findLoanById("fake-id"));
    }

    // ============ Exception Handling Tests ============

    @Test
    @DisplayName("borrowItem throws exception when user is null")
    void borrowItem_whenUserNull_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> repo.borrowItem(null, book));
    }

    @Test
    @DisplayName("borrowItem throws exception when item is null")
    void borrowItem_whenItemNull_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> repo.borrowItem(alice, null));
    }

    @Test
    @DisplayName("borrowItem throws exception when item is not available")
    void borrowItem_whenNotAvailable_throwsException() {
        book.setAvailable(false);
        FileBookRepository.getInstance().updateBooks(book);

        assertThrows(IllegalStateException.class, () -> repo.borrowItem(alice, book));
    }

    @Test
    @DisplayName("returnItem returns false when loan not found")
    void returnItem_whenLoanNotFound_returnsFalse() {
        assertFalse(repo.returnItem("non-existent-id", LocalDate.now()));
    }

    @Test
    @DisplayName("returnItem returns false when loan already returned")
    void returnItem_whenAlreadyReturned_returnsFalse() {
        Loan loan = repo.borrowItem(alice, book);
        repo.returnItem(loan.getLoanId(), LocalDate.now());

        assertFalse(repo.returnItem(loan.getLoanId(), LocalDate.now()));
    }

    @Test
    @DisplayName("returnItem uses current date when returnDate is null")
    void returnItem_whenReturnDateNull_usesCurrentDate() {
        Loan loan = repo.borrowItem(alice, book);

        boolean result = repo.returnItem(loan.getLoanId(), null);

        assertTrue(result);
        assertEquals(LocalDate.now(), loan.getReturnDate());
    }

    // ============ CD Support Tests ============

    @Test
    @DisplayName("borrowItem with CD creates loan correctly")
    void borrowItem_withCD_updatesCDAvailability() {
        CD cd = new CD("Greatest Hits", "Artist", "CD-001");
        cd.setAvailable(true);
        FileCDRepository.getInstance().saveCD(cd, 1);

        FileLoanRepository newRepo = new FileLoanRepository();
        CD loadedCD = FileCDRepository.getInstance().findByIsbn("CD-001");

        newRepo.borrowItem(alice, loadedCD);

        assertEquals(1, newRepo.getAllActiveLoans().size());
    }

    @Test
    @DisplayName("returnItem with CD updates CD availability correctly")
    void returnItem_withCD_updatesCDAvailability() {
        CD cd = new CD("Pop Album", "Singer", "CD-002");
        cd.setAvailable(true);
        FileCDRepository.getInstance().saveCD(cd, 1);

        FileLoanRepository newRepo = new FileLoanRepository();
        CD loadedCD = FileCDRepository.getInstance().findByIsbn("CD-002");

        Loan loan = newRepo.borrowItem(alice, loadedCD);
        newRepo.returnItem(loan.getLoanId(), LocalDate.now());

        assertTrue(loadedCD.isAvailable());
    }

    // ============ Overdue Loans Tests ============

    @Test
    @DisplayName("getOverdueLoans returns only overdue active loans")
    void getOverdueLoans_returnsOnlyOverdueLoans() throws Exception {
        repo.borrowItem(alice, book);

        Book oldBook = new Book("Old Book", "X", "777");
        oldBook.setAvailable(false);
        Loan oldLoan = new Loan("OLD123", alice, oldBook, LocalDate.now().minusDays(40));

        Field loansField = repo.getClass().getDeclaredField("loans");
        loansField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Loan> loansList = (List<Loan>) loansField.get(repo);
        loansList.add(oldLoan);

        List<Loan> overdue = repo.getOverdueLoans(LocalDate.now());
        assertEquals(1, overdue.size());
        assertEquals("OLD123", overdue.get(0).getLoanId());
    }

    // ============ File Loading Tests ============

    @Test
    @DisplayName("loadLoans correctly loads from file after restart")
    void loadLoans_persistsDataAfterRestart() {
        repo.borrowItem(alice, book);
        String loanId = repo.getAllActiveLoans().get(0).getLoanId();

        FileLoanRepository newRepo = new FileLoanRepository();

        Loan loadedLoan = newRepo.findLoanById(loanId);
        assertNotNull(loadedLoan);
        assertEquals("alice", loadedLoan.getUser().getUsername());
        assertFalse(loadedLoan.getMediaItem().isAvailable());
    }

    @Test
    @DisplayName("loadLoans handles corrupted line with wrong number of parts")
    void loadLoans_handlesCorruptedLine() throws IOException {

        String validLine = "LOAN123;alice;999;" + LocalDate.now() + ";NULL\n";
        String corruptedLine = "corrupted;line;only\n";

        Files.writeString(tempLoansFile, validLine + corruptedLine);


        FileLoanRepository.reset();
        FileLoanRepository newRepo = FileLoanRepository.getInstance();


        assertEquals(1, newRepo.getAllActiveLoans().size());
        assertNotNull(newRepo.findLoanById("LOAN123"));
    }

    @Test
    @DisplayName("loadLoans skips loan when media item does not exist")
    void loadLoans_skipsLoanWithNonExistentMedia() throws IOException {
        Files.writeString(tempLoansFile,
                "LOAN999;alice;FAKE-ISBN-999;2024-01-01;NULL\n");

        FileLoanRepository newRepo = new FileLoanRepository();

        assertEquals(0, newRepo.getAllActiveLoans().size());
    }

    @Test
    @DisplayName("loadLoans correctly loads returned loans")
    void loadLoans_loadsReturnedLoans() throws IOException {
        Loan loan = repo.borrowItem(alice, book);
        repo.returnItem(loan.getLoanId(), LocalDate.now().minusDays(2));

        FileLoanRepository newRepo = new FileLoanRepository();

        Loan loadedLoan = newRepo.findLoanById(loan.getLoanId());
        assertNotNull(loadedLoan);
        assertNotNull(loadedLoan.getReturnDate());
        assertTrue(loadedLoan.getMediaItem().isAvailable());
    }

    // ============ Edge Cases Tests ============

    @Test
    @DisplayName("findMediaItemById returns null when neither book nor CD exists")
    void findMediaItemById_returnsNullWhenNotFound() throws Exception {
        Method method = repo.getClass().getDeclaredMethod("findMediaItemById", String.class);
        method.setAccessible(true);

        MediaItem result = (MediaItem) method.invoke(repo, "NON-EXISTENT-ISBN");

        assertNull(result);
    }

    @Test
    @DisplayName("findUserByUsername creates customer user for non-admin")
    void findUserByUsername_createsCustomerUser() throws Exception {
        Method method = repo.getClass().getDeclaredMethod("findUserByUsername", String.class);
        method.setAccessible(true);

        User result = (User) method.invoke(repo, "testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("customer", result.getRole());
    }

    @Test
    @DisplayName("findUserByUsername creates admin user for admin username")
    void findUserByUsername_createsAdminUser() throws Exception {
        Method method = repo.getClass().getDeclaredMethod("findUserByUsername", String.class);
        method.setAccessible(true);

        User result = (User) method.invoke(repo, "admin");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("admin", result.getRole());
    }

    @Test
    @DisplayName("loadLoans handles empty file gracefully")
    void loadLoans_handlesEmptyFile() {
        FileLoanRepository newRepo = new FileLoanRepository();

        assertEquals(0, newRepo.getAllActiveLoans().size());
    }

    @Test
    @DisplayName("saveToFile handles multiple loans correctly")
    void saveToFile_handlesMultipleLoans() {
        Book book2 = new Book("Python", "Author2", "888");
        FileBookRepository.getInstance().saveBook(book2, 1);
        FileLoanRepository newRepo = new FileLoanRepository();

        newRepo.borrowItem(alice, book);
        newRepo.borrowItem(alice, book2);

        assertEquals(2, countLines(tempLoansFile));
        assertEquals(2, newRepo.getAllActiveLoans().size());
    }

    // ============ Helper Methods ============

    private int countLines(Path path) {
        try {
            return (int) Files.lines(path).count();
        } catch (IOException e) {
            return 0;
        }
    }

    private String readFirstLine(Path path) {
        try {
            return Files.lines(path).findFirst().orElse("");
        } catch (IOException e) {
            return "";
        }
    }
}
