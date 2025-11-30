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

    @Test
    void testGetOverdueLoans_EmptyList() {
        LocalDate today = LocalDate.now();
        when(mockLoanRepo.getOverdueLoans(today)).thenReturn(List.of());

        List<Loan> result = librarianService.getOverdueLoans(today);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetOverdueLoans_MultipleLoans() {
        LocalDate today = LocalDate.now();
        Loan anotherOverdue = new Loan("L4", user, book2, LocalDate.now().minusDays(40));

        when(mockLoanRepo.getOverdueLoans(today)).thenReturn(List.of(loanOverdue, anotherOverdue));

        List<Loan> result = librarianService.getOverdueLoans(today);

        assertEquals(2, result.size());
    }

    @Test
    void testGetOverdueLoansForUser_NoLoans() {
        LocalDate today = LocalDate.now();
        when(mockLoanRepo.getActiveLoansForUser("anySome")).thenReturn(List.of());

        List<Loan> result = librarianService.getOverdueLoansForUser("anySome", today);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetOverdueLoansForUser_Loans() {
        LocalDate today = LocalDate.now();
        when(mockLoanRepo.getActiveLoansForUser("sami")).thenReturn(List.of(loanOverdue));

        List<Loan> result = librarianService.getOverdueLoansForUser("sami", today);

        assertNotNull(result);
        assertEquals(0, result.size());
    }



    @Test
    void testGetOverdueLoansForUser_AllActiveNoOverdue() {
        LocalDate today = LocalDate.now();
        Loan active1 = new Loan("L5", user, book1, LocalDate.now().minusDays(1));
        Loan active2 = new Loan("L6", user, book2, LocalDate.now().minusDays(3));

        when(mockLoanRepo.getActiveLoansForUser("sami")).thenReturn(List.of(active1, active2));

        List<Loan> result = librarianService.getOverdueLoansForUser("sami", today);

        assertEquals(0, result.size());
    }

    @Test
    void testCalculateTotalFineDue_EmptyList() {
        LocalDate today = LocalDate.now();
        when(mockLoanRepo.getOverdueLoans(today)).thenReturn(List.of());

        int totalFine = librarianService.calculateTotalFineDue(today);

        assertEquals(0, totalFine);
    }

    @Test
    void testCalculateTotalFineDue_MultipleLoans() {
        LocalDate today = LocalDate.now();
        Loan anotherOverdue = new Loan("L4", user, book2, LocalDate.now().minusDays(35));
        when(mockLoanRepo.getOverdueLoans(today)).thenReturn(List.of(loanOverdue, anotherOverdue));

        int totalFine = librarianService.calculateTotalFineDue(today);

        assertEquals(90, totalFine);
    }

    @Test
    void testCalculateTotalFineDue_IncludesReturnedLoan() {
        LocalDate today = LocalDate.now();

        when(mockLoanRepo.getOverdueLoans(today)).thenReturn(List.of(loanReturned));

        int totalFine = librarianService.calculateTotalFineDue(today);

        assertEquals(0, totalFine);
    }

}
