// java
package Service;

import Domain.Book;
import Domain.Loan;
import Domain.User;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReminderServiceUnitTest {

    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUpOutput() {
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreOutput() {
        if (originalOut != null) System.setOut(originalOut);
    }


    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    private void callInit(ReminderService svc) throws Exception {
        Method m = ReminderService.class.getDeclaredMethod("initializeOverdueData");
        m.setAccessible(true);
        m.invoke(svc);
    }

    @Test
    void sendReminders_sendsEmail_whenUserHasEmail() throws Exception {
        // mocks
        FileLoanRepository loanRepo = mock(FileLoanRepository.class);
        FileUserRepository userRepo = mock(FileUserRepository.class);
        EmailService emailService = mock(EmailService.class);

        Loan loan = mock(Loan.class);
        User user = mock(User.class);
        Book media = mock(Book.class);

        LocalDate today = LocalDate.now();

        when(user.getUsername()).thenReturn("activeuser");
        when(user.getEmail()).thenReturn("user@example.com");

        when(media.getTitle()).thenReturn("Some Book");
        when(media.getAuthor()).thenReturn("Author");
        // mock due date on Loan (Book doesn't have getDueDate)
        when(loan.getDueDate()).thenReturn(today.minusDays(10));

        when(loan.getUser()).thenReturn(user);
        when(loan.getMediaItem()).thenReturn(media);
        when(loan.calculateFine(any())).thenReturn(50);
        when(loan.getOverdueDays(any())).thenReturn(10);

        when(loanRepo.getOverdueLoans(any())).thenReturn(Collections.singletonList(loan));
        when(userRepo.findUserByUsername("activeuser")).thenReturn(user);

        // construct service and inject mocks
        ReminderService svc = new ReminderService();
        setField(svc, "loanRepository", loanRepo);
        setField(svc, "userRepository", userRepo);
        setField(svc, "emailService", emailService);

        // rebuild internal overdue data with our mocks
        callInit(svc);

        // action
        svc.sendReminders();

        // verify email send called and output mentions reminder
        verify(emailService, times(1)).sendEmailAsync(eq("user@example.com"), anyString(), contains("Some Book"));
        String out = outContent.toString().toLowerCase();
        assertTrue(out.contains("reminder sent") || out.contains("activeuser") || out.contains("user@example.com"));
    }

    @Test
    void sendReminders_skipsUsersWithoutEmail_andLogs() throws Exception {
        FileLoanRepository loanRepo = mock(FileLoanRepository.class);
        FileUserRepository userRepo = mock(FileUserRepository.class);
        EmailService emailService = mock(EmailService.class);

        Loan loan = mock(Loan.class);
        User user = mock(User.class);

        when(user.getUsername()).thenReturn("noemail");
        when(user.getEmail()).thenReturn("");
        when(loan.getUser()).thenReturn(user);
        when(loan.calculateFine(any())).thenReturn(0);
        when(loan.getOverdueDays(any())).thenReturn(5);

        when(loanRepo.getOverdueLoans(any())).thenReturn(Collections.singletonList(loan));
        when(userRepo.findUserByUsername("noemail")).thenReturn(user);

        ReminderService svc = new ReminderService();
        setField(svc, "loanRepository", loanRepo);
        setField(svc, "userRepository", userRepo);
        setField(svc, "emailService", emailService);
        callInit(svc);

        svc.sendReminders();

        verify(emailService, never()).sendEmailAsync(anyString(), anyString(), anyString());
        String out = outContent.toString().toLowerCase();
        assertTrue(out.contains("has no email") || out.contains("skipping") || out.contains("no email"));
    }

    @Test
    void displayOverdueUsers_showsCorrectCountsAndFines() throws Exception {
        FileLoanRepository loanRepo = mock(FileLoanRepository.class);
        FileUserRepository userRepo = mock(FileUserRepository.class);

        Loan l1 = mock(Loan.class);
        Loan l2 = mock(Loan.class);
        User u1 = mock(User.class);
        Book m1 = mock(Book.class);

        LocalDate today = LocalDate.now();

        when(u1.getUsername()).thenReturn("userA");
        when(l1.getUser()).thenReturn(u1);
        when(l1.calculateFine(today)).thenReturn(20);
        when(l1.getOverdueDays(today)).thenReturn(2);
        when(l1.getMediaItem()).thenReturn(m1);
        when(m1.getTitle()).thenReturn("B1");
        when(m1.getAuthor()).thenReturn("A1");
        // mock due date on Loan instead of Book
        when(l1.getDueDate()).thenReturn(today.minusDays(3));

        when(l2.getUser()).thenReturn(u1);
        when(l2.calculateFine(today)).thenReturn(30);
        when(l2.getOverdueDays(today)).thenReturn(3);
        when(l2.getMediaItem()).thenReturn(m1);

        when(loanRepo.getOverdueLoans(any())).thenReturn(List.of(l1, l2));
        when(userRepo.findUserByUsername("userA")).thenReturn(u1);

        ReminderService svc = new ReminderService();
        setField(svc, "loanRepository", loanRepo);
        setField(svc, "userRepository", userRepo);

        // rebuild overdue data and exercise display
        callInit(svc);

        int count = svc.displayOverdueUsers();
        String out = outContent.toString();

        assertEquals(1, count);
        assertTrue(out.contains("userA") || out.contains("userA"));
        assertTrue(out.contains("Total fine") || out.toLowerCase().contains("total fine"));
    }

    @Test
    void buildReminderEmail_containsLoanDetails() throws Exception {
        // create a loan mock with values and call private buildReminderEmail to inspect content
        Loan loan = mock(Loan.class);
        User user = mock(User.class);
        Book media = mock(Book.class);

        when(user.getUsername()).thenReturn("buildUser");
        when(media.getTitle()).thenReturn("TitleX");
        when(media.getAuthor()).thenReturn("AuthX");
        LocalDate due = LocalDate.now().minusDays(5);
        // mock due date on Loan instead of Book
        when(loan.getDueDate()).thenReturn(due);
        when(loan.getMediaItem()).thenReturn(media);
        when(loan.getOverdueDays(any())).thenReturn(5);
        when(loan.calculateFine(any())).thenReturn(50);

        ReminderService svc = new ReminderService();
        // call private buildReminderEmail(User, List<Loan>, int)
        Method m = ReminderService.class.getDeclaredMethod("buildReminderEmail", User.class, List.class, int.class);
        m.setAccessible(true);
        String email = (String) m.invoke(svc, user, List.of(loan), 50);

        assertTrue(email.contains("Dear buildUser"));
        assertTrue(email.contains("TitleX"));
        assertTrue(email.contains("AuthX"));
        assertTrue(email.contains("TOTAL FINE") || email.toUpperCase().contains("TOTAL FINE"));
    }
}
