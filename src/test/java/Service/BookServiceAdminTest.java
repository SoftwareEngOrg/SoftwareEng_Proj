package Service;

import Domain.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookServiceAdmin Tests")
class BookServiceAdminTest {

    @Mock
    private FileLoanRepository mockLoanRepo;

    @Mock
    private FileUserRepository mockUserRepo;

    @Mock
    private FileCDRepository mockCDRepo;

    @Mock
    private FileBookRepository mockBookRepo;

    @InjectMocks
    private BookServiceAdmin bookServiceAdmin;

    private Book testBook;
    private CD testCD;
    private User testUser;

    @BeforeEach
    void setUp() {
        testBook = new Book("Test Book", "Test Author", "ISBN123");
        testCD = new CD("Test CD", "Test Artist", "CD123");
        testUser = new User("testuser", "password", "customer");
    }

    // ==================== addBook Tests ====================

    @Test
    @DisplayName("Should return false when number of copies is zero")
    void addBook_WithZeroCopies_ReturnsFalse() {
        boolean result = bookServiceAdmin.addBook(testBook, 0);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when number of copies is negative")
    void addBook_WithNegativeCopies_ReturnsFalse() {
        boolean result = bookServiceAdmin.addBook(testBook, -5);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when book with same ISBN already exists")
    void addBook_WithExistingISBN_ReturnsFalse() {

        List<Book> existingBooks = new ArrayList<>();
        existingBooks.add(new Book("Existing Book", "Author", "ISBN123"));
        when(mockBookRepo.findAllBooks()).thenReturn(existingBooks);
        when(mockCDRepo.findByIsbn(anyString())).thenReturn(null);


        boolean result = bookServiceAdmin.addBook(testBook, 3);


        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when CD with same ISBN exists")
    void addBook_WithExistingCDISBN_ReturnsFalse() {

        when(mockBookRepo.findAllBooks()).thenReturn(new ArrayList<>());
        when(mockCDRepo.findByIsbn("ISBN123")).thenReturn(testCD);


        boolean result = bookServiceAdmin.addBook(testBook, 3);


        assertFalse(result);
    }

    @Test
    @DisplayName("Should successfully add book when all validations pass")
    void addBook_WithValidData_ReturnsTrue() {

        when(mockBookRepo.findAllBooks()).thenReturn(new ArrayList<>());
        when(mockCDRepo.findByIsbn(anyString())).thenReturn(null);


        boolean result = bookServiceAdmin.addBook(testBook, 5);


        assertTrue(result);
    }

    // ==================== addCD Tests ====================

    @Test
    @DisplayName("Should return false when CD copies is zero")
    void addCD_WithZeroCopies_ReturnsFalse() {
        boolean result = bookServiceAdmin.addCD(testCD, 0);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when CD copies is negative")
    void addCD_WithNegativeCopies_ReturnsFalse() {
        boolean result = bookServiceAdmin.addCD(testCD, -3);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when CD with same ISBN already exists")
    void addCD_WithExistingISBN_ReturnsFalse() {

        List<CD> existingCDs = new ArrayList<>();
        existingCDs.add(new CD("Existing CD", "Artist", "CD123"));
        when(mockCDRepo.findAllCDs()).thenReturn(existingCDs);
        when(mockBookRepo.findByIsbn(anyString())).thenReturn(null);


        boolean result = bookServiceAdmin.addCD(testCD, 3);


        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when Book with same ISBN exists")
    void addCD_WithExistingBookISBN_ReturnsFalse() {

        when(mockCDRepo.findAllCDs()).thenReturn(new ArrayList<>());
        when(mockBookRepo.findByIsbn("CD123")).thenReturn(testBook);


        boolean result = bookServiceAdmin.addCD(testCD, 3);


        assertFalse(result);
    }

    @Test
    @DisplayName("Should successfully add CD when all validations pass")
    void addCD_WithValidData_ReturnsTrue() {

        when(mockCDRepo.findAllCDs()).thenReturn(new ArrayList<>());
        when(mockBookRepo.findByIsbn(anyString())).thenReturn(null);


        boolean result = bookServiceAdmin.addCD(testCD, 5);


        assertTrue(result);
    }

    // ==================== view InactiveUsers Tests ====================

    @Test
    @DisplayName("Should return empty list when no users exist")
    void viewInactiveUsers_WithNoUsers_ReturnsEmptyList() {

        when(mockLoanRepo.getAllActiveLoans()).thenReturn(new ArrayList<>());
        when(mockUserRepo.getAllUsers()).thenReturn(new ArrayList<>());


        List<User> result = bookServiceAdmin.viewInactiveUsers();


        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when all users are active")
    void viewInactiveUsers_WithAllActiveUsers_ReturnsEmptyList() {

        User user1 = new User("user1", "pass", "customer");
        User user2 = new User("user2", "pass", "customer");

        Loan loan1 = new Loan("L1", user1, testBook, java.time.LocalDate.now());
        Loan loan2 = new Loan("L2", user2, testCD, java.time.LocalDate.now());

        when(mockLoanRepo.getAllActiveLoans()).thenReturn(Arrays.asList(loan1, loan2));
        when(mockUserRepo.getAllUsers()).thenReturn(Arrays.asList(user1, user2));


        List<User> result = bookServiceAdmin.viewInactiveUsers();


        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return inactive customer users only")
    void viewInactiveUsers_WithInactiveCustomers_ReturnsInactiveList() {

        User activeUser = new User("active", "pass", "customer");
        User inactiveUser = new User("inactive", "pass", "customer");
        User adminUser = new User("admin", "pass", "admin");

        Loan activeLoan = new Loan("L1", activeUser, testBook, java.time.LocalDate.now());

        when(mockLoanRepo.getAllActiveLoans()).thenReturn(Collections.singletonList(activeLoan));
        when(mockUserRepo.getAllUsers()).thenReturn(Arrays.asList(activeUser, inactiveUser, adminUser));


        List<User> result = bookServiceAdmin.viewInactiveUsers();


        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should not include admin users in inactive list")
    void viewInactiveUsers_WithInactiveAdmin_ExcludesAdmin() {

        User inactiveCustomer = new User("customer", "pass", "customer");
        User inactiveAdmin = new User("admin", "pass", "admin");

        when(mockLoanRepo.getAllActiveLoans()).thenReturn(new ArrayList<>());
        when(mockUserRepo.getAllUsers()).thenReturn(Arrays.asList(inactiveCustomer, inactiveAdmin));


        List<User> result = bookServiceAdmin.viewInactiveUsers();


        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should handle multiple inactive users correctly")
    void viewInactiveUsers_WithMultipleInactive_ReturnsAllInactive() {

        User inactive1 = new User("inactive1", "pass", "customer");
        User inactive2 = new User("inactive2", "pass", "customer");
        User inactive3 = new User("inactive3", "pass", "customer");

        when(mockLoanRepo.getAllActiveLoans()).thenReturn(new ArrayList<>());
        when(mockUserRepo.getAllUsers()).thenReturn(Arrays.asList(inactive1, inactive2, inactive3));


        List<User> result = bookServiceAdmin.viewInactiveUsers();


        assertEquals(3, result.size());
    }

    // ==================== unregisterAllUsers Tests ====================

    @Test
    @DisplayName("Should call userFile.unregisterAllUsers with provided list")
    void unregisterAllUsers_WithUserList_CallsRepository() {

        List<User> users = Arrays.asList(testUser);


        bookServiceAdmin.unregisterAllUsers(users);


        verify(mockUserRepo, times(1)).unregisterAllUsers(users);
    }

    @Test
    @DisplayName("Should handle empty user list")
    void unregisterAllUsers_WithEmptyList_CallsRepository() {

        List<User> emptyList = new ArrayList<>();


        bookServiceAdmin.unregisterAllUsers(emptyList);


        verify(mockUserRepo, times(1)).unregisterAllUsers(emptyList);
    }

    @Test
    @DisplayName("Should handle null user list gracefully")
    void unregisterAllUsers_WithNullList_CallsRepository() {

        assertDoesNotThrow(() -> bookServiceAdmin.unregisterAllUsers(null));
    }

    // ==================== unregisterUserByUsername Tests ====================

    @Test
    @DisplayName("Should print success message when user is unregistered successfully")
    void unregisterUserByUsername_WithValidUser_PrintsSuccess() {

        when(mockUserRepo.unregisterUserByUsername("testuser")).thenReturn(true);


        bookServiceAdmin.unregisterUserByUsername("testuser");


        verify(mockUserRepo, times(1)).unregisterUserByUsername("testuser");
    }

    @Test
    @DisplayName("Should print error message when unregistration fails")
    void unregisterUserByUsername_WithInvalidUser_PrintsError() {

        when(mockUserRepo.unregisterUserByUsername("nonexistent")).thenReturn(false);


        bookServiceAdmin.unregisterUserByUsername("nonexistent");


        verify(mockUserRepo, times(1)).unregisterUserByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should handle null username gracefully")
    void unregisterUserByUsername_WithNullUsername_CallsRepository() {

        when(mockUserRepo.unregisterUserByUsername(null)).thenReturn(false);


        bookServiceAdmin.unregisterUserByUsername(null);


        verify(mockUserRepo, times(1)).unregisterUserByUsername(null);
    }

    @Test
    @DisplayName("Should handle empty username")
    void unregisterUserByUsername_WithEmptyUsername_CallsRepository() {

        when(mockUserRepo.unregisterUserByUsername("")).thenReturn(false);


        bookServiceAdmin.unregisterUserByUsername("");


        verify(mockUserRepo, times(1)).unregisterUserByUsername("");
    }
}