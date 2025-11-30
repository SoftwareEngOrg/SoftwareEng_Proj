// File: src/test/java/Service/FileLoanRepositoryTest.java

package Service;

import Domain.Book;
import Domain.Loan;
import Domain.User;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileLoanRepositoryTest {

    private Path tempLoansFile;
    private Path tempBooksFile;
    private FileLoanRepository repo;
    private User alice;
    private Book book;

    @BeforeAll
    void setUpTempFiles() throws IOException {
        tempLoansFile = Files.createTempFile("test_loans", ".txt");
        tempBooksFile = Files.createTempFile("test_books", ".txt");
        FileLoanRepository.FILE_PATH = tempLoansFile.toString();  // override static
        FileBookRepository.repoPath = tempBooksFile.toString();   // override static
    }

    @BeforeEach
    void setUp() throws IOException {
        Files.writeString(tempLoansFile, "");   // empty loans file
        Files.writeString(tempBooksFile, "");   // empty books file

        repo = new FileLoanRepository();       // loads empty files
        alice = new User("alice", "123", "customer");
        book = new Book("Java Guide", "Ahmed", "999");
        book.setAvailable(true);

        // Save the book so it exists in the repo
        FileBookRepository.getInstance().saveBook(book);
        repo = new FileLoanRepository();  // reload so book is cached
    }

    @AfterAll
    void cleanUp() throws IOException {
        Files.deleteIfExists(tempLoansFile);
        Files.deleteIfExists(tempBooksFile);
    }

    @Test
    @DisplayName("borrowItem creates loan, sets book unavailable, and saves to file")
    void borrowItem_worksCorrectly() {
        Loan loan = repo.borrowItem(alice, book);

        assertNotNull(loan.getLoanId());
        assertEquals("alice", loan.getUser().getUsername());
        assertEquals("Java Guide", loan.getMediaItem().getTitle());
        assertEquals(LocalDate.now(), loan.getBorrowDate());

        assertFalse(book.isAvailable());               // in memory
        assertEquals(1, repo.getAllActiveLoans().size()); // in list
        assertEquals(1, countLines(tempLoansFile));    // in file
    }

    @Test
    @DisplayName("borrowItem throws exception when item is not available")
    void borrowItem_whenNotAvailable_throwsException() {
        book.setAvailable(false);
        FileBookRepository.getInstance().updateBooks(book);  // save the unavailable state

        assertThrows(IllegalStateException.class, () -> {
            repo.borrowItem(alice, book);
        });
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
        assertEquals(0, repo.getAllActiveLoans().size()); // no longer active
        assertEquals(1, countLines(tempLoansFile));       // still in file (with return date)
        assertTrue(readFirstLine(tempLoansFile).endsWith(returnDate.toString()));
    }

    @Test
    @DisplayName("returnItem returns false when loan not found or already returned")
    void returnItem_whenNotFoundOrReturned_returnsFalse() {
        Loan loan = repo.borrowItem(alice, book);
        repo.returnItem(loan.getLoanId(), LocalDate.now()); // return it first

        assertFalse(repo.returnItem(loan.getLoanId(), LocalDate.now()));
        assertFalse(repo.returnItem("wrong-id", LocalDate.now()));
    }

    @Test
    @DisplayName("getActiveLoansForUser returns only active loans of the user")
    void getActiveLoansForUser_worksCorrectly() {
        repo.borrowItem(alice, book);

        User bob = new User("bob", "456", "customer");
        Book book2 = new Book("Python", "B", "888");
        FileBookRepository.getInstance().saveBook(book2);
        repo = new FileLoanRepository();
        repo.borrowItem(bob, book2);

        List<Loan> aliceLoans = repo.getActiveLoansForUser("alice");
        assertEquals(1, aliceLoans.size());
        assertEquals("alice", aliceLoans.get(0).getUser().getUsername());
    }

    @Test
    @DisplayName("getAllActiveLoans returns only active loans")
    void getAllActiveLoans_worksCorrectly() {
        Loan loan = repo.borrowItem(alice, book);
        repo.returnItem(loan.getLoanId(), LocalDate.now());

        assertEquals(0, repo.getAllActiveLoans().size());
    }

    @Test
    @DisplayName("getOverdueLoans returns only overdue loans")
    void getOverdueLoans_worksCorrectly() throws NoSuchFieldException, IllegalAccessException {
        repo.borrowItem(alice, book);  // borrowed today → not overdue

        // Create an old loan (simulate by directly adding)
        Loan oldLoan = new Loan("OLD123", alice, book, LocalDate.now().minusDays(40));
        book.setAvailable(true);
        repo.borrowItem(alice, book); // just to make sure book is loaded

        // Force add old loan (for test only)
        repo.getClass().getDeclaredField("loans").setAccessible(true);
        ((List<Loan>) repo.getClass().getDeclaredField("loans").get(repo)).add(oldLoan);

        List<Loan> overdue = repo.getOverdueLoans(LocalDate.now());
        assertEquals(1, overdue.size());
        assertEquals("OLD123", overdue.get(0).getLoanId());
    }

    @Test
    @DisplayName("findLoanById returns correct loan or null")
    void findLoanById_worksCorrectly() {
        Loan loan = repo.borrowItem(alice, book);

        assertEquals(loan, repo.findLoanById(loan.getLoanId()));
        assertNull(repo.findLoanById("fake-id"));
    }

    @Test
    @DisplayName("loadLoans correctly loads from file after restart")
    void loadLoans_worksAfterRestart() throws IOException {
        repo.borrowItem(alice, book);
        String loanId = repo.getAllActiveLoans().get(0).getLoanId();

        // New repository = simulate app restart
        FileLoanRepository newRepo = new FileLoanRepository();

        Loan loadedLoan = newRepo.findLoanById(loanId);
        assertNotNull(loadedLoan);
        assertEquals("alice", loadedLoan.getUser().getUsername());
        assertFalse(loadedLoan.getMediaItem().isAvailable());
    }

    // Helper methods
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