// src/test/java/Domain/LoanTest.java

package Domain;

import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;



class LoanTest {

    private User user;
    private Book book;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        user = new User("alice", "pass", "customer");
        book = new Book("Clean Code", "Robert Martin", "978-0132350884");
        today = LocalDate.of(2025, 4, 5);
    }

    @Test
    @DisplayName("getOverdueDays returns 0 when not overdue (branch: if condition false)")
    void getOverdueDays_whenNotOverdue_returnsZero() {
        Loan loan = new Loan("L1", user, book, today.minusDays(10));

        int overdueDays = loan.getOverdueDays(today);

        assertEquals(0, overdueDays);
    }

    @Test
    @DisplayName("getOverdueDays returns correct days when overdue (branch: if condition true)")
    void getOverdueDays_whenOverdue_returnsCorrectDays() {
        Loan loan = new Loan("L2", user, book, today.minusDays(40));


        int overdueDays = loan.getOverdueDays(today);

        assertEquals(12, overdueDays);
    }

    @Test
    @DisplayName("getOverdueDays returns 0 after book is returned")
    void getOverdueDays_afterReturn_returnsZero() {
        Loan loan = new Loan("L3", user, book, today.minusDays(50));
        loan.returnItem(today.minusDays(5));

        assertEquals(0, loan.getOverdueDays(today));
    }

    @Test
    @DisplayName("All getters return correct values")
    void getters_returnCorrectValues() {
        Loan loan = new Loan("LOAN123", user, book, today.minusDays(15));

        assertEquals("LOAN123", loan.getLoanId());
        assertEquals(user, loan.getUser());
        assertEquals(book, loan.getMediaItem());
        assertEquals(today.minusDays(15), loan.getBorrowDate());
        assertEquals(today.minusDays(15).plusDays(28), loan.getDueDate()); // 28-day period
        assertNull(loan.getReturnDate());
    }

    @Test
    @DisplayName("returnItem sets returnDate and makes media available")
    void returnItem_setsReturnDateAndMakesItemAvailable() {
        book.setAvailable(false); // simulate borrowed
        Loan loan = new Loan("L4", user, book, today);

        loan.returnItem(today.plusDays(1));

        assertEquals(today.plusDays(1), loan.getReturnDate());
        assertTrue(book.isAvailable());
    }

    @Test
    @DisplayName("isOverdue returns true only when not returned and past due date")
    void isOverdue_comprehensiveCheck() {

        Loan overdue = new Loan("O1", user, book, today.minusDays(35));
        assertTrue(overdue.isOverdue(today));


        Loan onTime = new Loan("O2", user, book, today.minusDays(20));
        assertFalse(onTime.isOverdue(today));


        Loan returned = new Loan("O3", user, book, today.minusDays(40));
        returned.returnItem(today.minusDays(10));
        assertFalse(returned.isOverdue(today));
    }

    @Test
    @DisplayName("calculateFine returns correct amount when overdue")
    void calculateFine_returnsCorrectFine() {
        Loan loan = new Loan("F1", user, book, today.minusDays(32));

        assertEquals(40, loan.calculateFine(today));
    }



    @Test
    @DisplayName("toString contains loanId, title, username, and due date")
    void toString_containsExpectedInfo() {
        Loan loan = new Loan("ABC999", user, book, today);

        String str = loan.toString();

        assertTrue(str.contains("ABC999"));
        assertTrue(str.contains("Clean Code"));
        assertTrue(str.contains("alice"));
        assertTrue(str.contains(today.plusDays(28).toString())); // due date
    }
}