package Service;

import Domain.Book;
import Domain.Loan;
import Domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LibrarianServiceTest {

    @Mock
    private FileLoanRepository mockLoanRepo;

    @InjectMocks
    private LibrarianService librarianService;

    private User user;
    private Book book1;
    private Book book2;
    private Book book3;
    private Loan loanOverdue;
    private Loan loanActive;
    private Loan loanReturned;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("sami", "123", "customer");

        book1 = new Book("Java", "Author", "ISBN1");
        book2 = new Book("C++", "Author2", "ISBN2");
        book3 = new Book("Python", "Author3", "ISBN3");

        loanOverdue = new Loan("L1", user, book1, LocalDate.now().minusDays(30));
        loanActive = new Loan("L2", user, book2, LocalDate.now().minusDays(2));
        loanReturned = new Loan("L3", user, book3, LocalDate.now().minusDays(20));
        loanReturned.returnItem(LocalDate.now().minusDays(5));
    }

    // Test for getOverdueLoans() (without parameters, using LocalDate.now())
    @Test
    void testGetOverdueLoans_NoParameters() {
        LocalDate today = LocalDate.now();
        Loan anotherOverdue = new Loan("L4", user, book2, LocalDate.now().minusDays(40));

        when(mockLoanRepo.getOverdueLoans(today)).thenReturn(List.of(loanOverdue, anotherOverdue));

        List<Loan> result = librarianService.getOverdueLoans(); // Using no parameters, it should use LocalDate.now()

        assertEquals(2, result.size());
        verify(mockLoanRepo, times(1)).getOverdueLoans(today); // Ensure that the method is called with today's date
    }

    // Test for getAllActiveLoans()
    @Test
    void testGetAllActiveLoans() {
        when(mockLoanRepo.getAllActiveLoans()).thenReturn(List.of(loanActive, loanOverdue));

        List<Loan> result = librarianService.getAllActiveLoans();

        assertNotNull(result);
        assertEquals(2, result.size()); // It should return the active loans: loanActive and loanOverdue
    }

    // Test for getOverdueLoansForUser() (without currentDate parameter)
    @Test
    void testGetOverdueLoansForUser_NoDateParameter() {
        LocalDate today = LocalDate.now();
        Loan anotherOverdue = new Loan("L4", user, book2, LocalDate.now().minusDays(40));

        when(mockLoanRepo.getActiveLoansForUser("sami")).thenReturn(List.of(loanOverdue, anotherOverdue));

        List<Loan> result = librarianService.getOverdueLoansForUser("sami"); // Using no date parameter, it should use LocalDate.now()

        assertEquals(2, result.size());
        verify(mockLoanRepo, times(1)).getActiveLoansForUser("sami"); // Ensure that the method is called with the username
    }

    @Test
    void testGetOverdueLoansForUser_WithDateParameter() {
        LocalDate today = LocalDate.now();
        Loan anotherOverdue = new Loan("L4", user, book2, LocalDate.now().minusDays(40));

        when(mockLoanRepo.getActiveLoansForUser("sami")).thenReturn(List.of(loanOverdue, anotherOverdue));

        List<Loan> result = librarianService.getOverdueLoansForUser("sami", today); // This method already handles the date parameter

        assertEquals(2, result.size());
    }

    @Test
    void testGetOverdueLoansForUser_NoOverdueLoans() {
        LocalDate today = LocalDate.now();
        when(mockLoanRepo.getActiveLoansForUser("sami")).thenReturn(List.of(loanActive));

        List<Loan> result = librarianService.getOverdueLoansForUser("sami", today);

        assertNotNull(result);
        assertEquals(0, result.size()); // No overdue loans for this user
    }

    @Test
    void testGetOverdueLoansForUser_WithOverdue() {
        LocalDate today = LocalDate.now();
        when(mockLoanRepo.getActiveLoansForUser("sami")).thenReturn(List.of(loanOverdue));

        List<Loan> result = librarianService.getOverdueLoansForUser("sami", today);

        assertNotNull(result);
        assertEquals(1, result.size()); // Only the overdue loan should be returned
    }
    @Test
    void testCalculateTotalFineDue_NoOverdueLoans() {
        when(mockLoanRepo.getOverdueLoans(LocalDate.now())).thenReturn(List.of());
        int result = librarianService.calculateTotalFineDue(LocalDate.now());
        assertEquals(0, result, "The total fine should be 0 when there are no overdue loans.");
        verify(mockLoanRepo, times(1)).getOverdueLoans(LocalDate.now());
    }
}
