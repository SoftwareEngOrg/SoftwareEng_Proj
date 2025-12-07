package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@DisplayName("BookInventory Tests")
class BookInventoryTest {

    private BookInventory inventory;

    @BeforeEach
    void setUp() {
        inventory = BookInventory.getInstance();
    }

    @Test
    @DisplayName("Singleton instance should not be null")
    void testSingletonInstance() {
        assertNotNull(BookInventory.getInstance());
    }

    @Test
    @DisplayName("Add observer should store observer")
    void testAddObserverStoresObserver() {
        BookObserver observer = mock(BookObserver.class);
        inventory.addObserver(observer);
        assertEquals(1, inventory.observers.size());
    }

    @Test
    @DisplayName("Notify book returned should call observer")
    void testNotifyBookReturnedCallsObserver() {
        BookObserver observer = mock(BookObserver.class);
        inventory.addObserver(observer);

        inventory.notifyBookReturned("12345");

        verify(observer, times(1)).onBookAvailable("12345");
    }

    @Test
    @DisplayName("Notify book returned should clear observers")
    void testNotifyBookReturnedClearsObservers() {
        BookObserver observer = mock(BookObserver.class);
        inventory.addObserver(observer);

        inventory.notifyBookReturned("12345");

        assertEquals(0, inventory.observers.size());
    }
}
