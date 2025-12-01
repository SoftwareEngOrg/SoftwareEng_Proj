package Service;
import Domain.Book;
import Domain.Loan;
import Domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class BookServiceCustomerTest {


    @Mock
    private FileBookRepository mockBookRepo;

    @Mock
    private FileLoanRepository mockLoanRepo;

    @InjectMocks
    private BookServiceCustomer bookServiceCustomer;

    private User user;
    private Book book;
    private Loan fakeLoan;


    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);

        user = new User("sami" , "123" , "customer");

        book = new Book("Java Basics", "Author Name", "12345");
        book.setAvailable(true);

        fakeLoan = new Loan("L100", user, book, LocalDate.now());

        bookServiceCustomer.setCurrentUser(user);

    }


    @Test
    void borrowBook_success()
    {
        // I have a user in bookCustomerService sami
        when(mockBookRepo.findAllBooks()).thenReturn(List.of(book));
        when(mockLoanRepo.getActiveLoansForUser("sami")).thenReturn(List.of());
        when(mockLoanRepo.borrowItem(any(), any())).thenReturn(fakeLoan);
        boolean result = bookServiceCustomer.borrowMediaItem("12345");
        assertTrue(result);
        verify(mockLoanRepo, times(1)).borrowItem(user, book);
    }

    @Test
    public void testBorrowBook_NoUser() {
        bookServiceCustomer.setCurrentUser(null);
        boolean result = bookServiceCustomer.borrowMediaItem("ISBN12345");
        assertFalse(result);
    }


    @Test
    public void testBorrowBook_BookNotFound()
    {
        when(mockBookRepo.findAllBooks()).thenReturn(List.of());
        boolean result = bookServiceCustomer.borrowMediaItem("ISBN12345");
        assertFalse(result);
    }


    @Test
    public void testBorrowBook_BookNotAvailable()
    {
        book.setAvailable(false);
        when(mockBookRepo.findAllBooks()).thenReturn(List.of(book));
        boolean result = bookServiceCustomer.borrowMediaItem("ISBN12345");
        assertFalse(result);
    }

    @Test
    public void testReturnBook_Success()
    {
        fakeLoan.returnItem(null);
        when(mockLoanRepo.findLoanById("L100")).thenReturn(fakeLoan);
        when(mockLoanRepo.getActiveLoansForUser("sami")).thenReturn(List.of());

        boolean result = bookServiceCustomer.returnBook("L100");

        assertTrue(result);
        verify(mockLoanRepo, times(1)).returnItem("L100", any(LocalDate.class));

    }


    @Test
    public void testReturnBook_AlreadyReturned() {
        fakeLoan.returnItem(LocalDate.now());
        when(mockLoanRepo.findLoanById("L100")).thenReturn(fakeLoan);

        boolean result = bookServiceCustomer.returnBook("L100");
        assertFalse(result);
    }


    @Test
    public void testReturnBook_NotUserLoan() {
        User user2 = new User("Hani" , "123" , "customer");
        fakeLoan = new Loan("L100", user2, book, LocalDate.now());
        fakeLoan.returnItem(null);

        when(mockLoanRepo.findLoanById("L100")).thenReturn(fakeLoan);

        boolean result = bookServiceCustomer.returnBook("L100");
        assertFalse(result);
    }


    @Test
    public void testReturnBook_WithOverdueFine() {

        LocalDate borrowDate = LocalDate.now().minusDays(30);
        fakeLoan = new Loan("L100", user, book, borrowDate);

        when(mockLoanRepo.findLoanById("L100")).thenReturn(fakeLoan);

        boolean result = bookServiceCustomer.returnBook("L100");
        assertFalse(result);
    }

    @Test
    public void testCompleteReturn_WithFinePaid() throws Exception {

        fakeLoan = new Loan("L100", user, book, LocalDate.now().minusDays(10));


        when(mockLoanRepo.findLoanById("L100")).thenReturn(fakeLoan);
        when(mockLoanRepo.returnItem(eq("L100"), any(LocalDate.class))).thenAnswer(inv -> {
            LocalDate today = inv.getArgument(1, LocalDate.class);
            fakeLoan.returnItem(today);
            return true;
        });


        boolean result = bookServiceCustomer.completeReturn("L100");


        assertTrue(result);
        assertNotNull(fakeLoan.getReturnDate());
    }










}
