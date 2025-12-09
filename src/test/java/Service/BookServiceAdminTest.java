package Service;

import Domain.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceAdminTest {


    @Mock FileLoanRepository loanFile;
    @Mock FileUserRepository userFile;
    @Mock FileBookRepository fileBook;
    @Mock FileCDRepository fileCD;


    private BookServiceAdmin admin;

    private Book book;
    private CD cd;
    private User cust1, cust2, adminUser;
    private Loan loan1;

    @BeforeEach
    void setUp() throws Exception {

        admin = new BookServiceAdmin();


        injectMock(admin, "loanFile", loanFile);
        injectMock(admin, "userFile", userFile);
        injectMock(admin, "fileCD", fileCD);


        injectMock(admin, "fileBook", fileBook);


        book = new Book("Title", "Author", "1234567890");
        cd = new CD("CD Title", "Artist", "9876543210");
        cust1 = new User("cust1", "pass", "customer");
        cust2 = new User("cust2", "pass", "customer");
        adminUser = new User("admin", "pass", "admin");
        loan1 = new Loan("L1", cust1, book, LocalDate.now());
    }


    private void injectMock(Object target, String fieldName, Object mock) throws Exception {
        Field field = findField(target.getClass(), fieldName);
        field.setAccessible(true);
        field.set(target, mock);
    }


    private Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findField(clazz.getSuperclass(), fieldName);
            }
            throw e;
        }
    }

    // ====================== addBook ======================

    @Test
    void addBook_invalidCopies_returnsFalse() {
        assertFalse(admin.addBook(book, 0));
        assertFalse(admin.addBook(book, -5));
    }

    @Test
    void addBook_alreadyExists_returnsFalse() throws Exception {
        try (MockedStatic<FileBookRepository> staticBook = mockStatic(FileBookRepository.class);
             MockedStatic<FileCDRepository> staticCD = mockStatic(FileCDRepository.class)) {

            staticBook.when(FileBookRepository::getInstance).thenReturn(fileBook);
            staticCD.when(FileCDRepository::getInstance).thenReturn(fileCD);

            when(fileBook.findAllBooks()).thenReturn(List.of(book));

            assertFalse(admin.addBook(book, 5));
            staticBook.verify(() -> FileBookRepository.saveBook(any(), anyInt()), never());
        }
    }

    @Test
    void addBook_isbnExistsInCD_returnsFalse() throws Exception {
        try (MockedStatic<FileBookRepository> staticBook = mockStatic(FileBookRepository.class);
             MockedStatic<FileCDRepository> staticCD = mockStatic(FileCDRepository.class)) {

            staticBook.when(FileBookRepository::getInstance).thenReturn(fileBook);
            staticCD.when(FileCDRepository::getInstance).thenReturn(fileCD);

            when(fileBook.findAllBooks()).thenReturn(List.of());
            when(fileCD.findByIsbn("1234567890")).thenReturn(cd);

            assertFalse(admin.addBook(book, 5));
        }
    }

    @Test
    void addBook_validBook_savesAndReturnsTrue() throws Exception {
        try (MockedStatic<FileBookRepository> staticBook = mockStatic(FileBookRepository.class);
             MockedStatic<FileCDRepository> staticCD = mockStatic(FileCDRepository.class)) {

            staticBook.when(FileBookRepository::getInstance).thenReturn(fileBook);
            staticCD.when(FileCDRepository::getInstance).thenReturn(fileCD);

            when(fileBook.findAllBooks()).thenReturn(List.of());
            when(fileCD.findByIsbn(anyString())).thenReturn(null);

            assertTrue(admin.addBook(book, 3));
            staticBook.verify(() -> FileBookRepository.saveBook(book, 3));
        }
    }

    // ====================== addCD ======================

    @Test
    void addCD_validCD_savesAndReturnsTrue() throws Exception {
        try (MockedStatic<FileBookRepository> staticBook = mockStatic(FileBookRepository.class);
             MockedStatic<FileCDRepository> staticCD = mockStatic(FileCDRepository.class)) {

            staticBook.when(FileBookRepository::getInstance).thenReturn(fileBook);
            staticCD.when(FileCDRepository::getInstance).thenReturn(fileCD);

            when(fileCD.findAllCDs()).thenReturn(List.of());
            when(fileBook.findByIsbn(anyString())).thenReturn(null);

            assertTrue(admin.addCD(cd, 2));
            staticCD.verify(() -> FileCDRepository.saveCD(cd, 2));
        }
    }

    // ====================== viewInactiveUsers ======================

    @Test
    void viewInactiveUsers_returnsOnlyInactiveCustomers() {
        when(loanFile.getAllActiveLoans()).thenReturn(List.of(loan1));
        when(userFile.getAllUsers()).thenReturn(List.of(cust1, cust2, adminUser));

        List<User> result = admin.viewInactiveUsers();

        assertEquals(1, result.size());
        assertEquals("cust2", result.get(0).getUsername());
    }

    @Test
    void viewInactiveUsers_noLoans_returnsAllCustomers() {
        when(loanFile.getAllActiveLoans()).thenReturn(List.of());
        when(userFile.getAllUsers()).thenReturn(List.of(cust1, cust2, adminUser));

        assertEquals(2, admin.viewInactiveUsers().size());
    }

    // ====================== unregister ======================

    @Test
    void unregisterAllUsers_callsRepository() {
        List<User> list = List.of(cust1);
        admin.unregisterAllUsers(list);
        verify(userFile).unregisterAllUsers(list);
    }

    @Test
    void unregisterUserByUsername_success_printsMessage() {
        when(userFile.unregisterUserByUsername("cust1")).thenReturn(true);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        admin.unregisterUserByUsername("cust1");

        assertTrue(out.toString().contains("successfully"));
    }

    @Test
    void unregisterUserByUsername_failure_printsError() {
        when(userFile.unregisterUserByUsername("unknown")).thenReturn(false);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        admin.unregisterUserByUsername("unknown");

        assertTrue(out.toString().contains("something went wrong"));
    }
}