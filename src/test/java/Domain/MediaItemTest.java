package Domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MediaItemTest {



    @Test
    @DisplayName("Book: getTitle returns initial title")
    void testBookGetTitle() {
        Book book = new Book("T1", "A1", "I1");
        assertEquals("T1", book.getTitle());
    }

    @Test
    @DisplayName("Book: setTitle updates title")
    void testBookSetTitle() {
        Book book = new Book("Old", "A1", "I1");
        book.setTitle("New");
        assertEquals("New", book.getTitle());
    }

    @Test
    @DisplayName("Book: getAuthor returns initial author")
    void testBookGetAuthor() {
        Book book = new Book("T1", "A1", "I1");
        assertEquals("A1", book.getAuthor());
    }

    @Test
    @DisplayName("Book: setAuthor updates author")
    void testBookSetAuthor() {
        Book book = new Book("T1", "Old", "I1");
        book.setAuthor("New");
        assertEquals("New", book.getAuthor());
    }

    @Test
    @DisplayName("Book: isAvailable returns default true")
    void testBookGetAvailability() {
        Book book = new Book("T1", "A1", "I1");
        assertTrue(book.isAvailable());
    }

    @Test
    @DisplayName("Book: setAvailable updates availability")
    void testBookSetAvailability() {
        Book book = new Book("T1", "A1", "I1");
        book.setAvailable(false);
        assertFalse(book.isAvailable());
    }

    @Test
    @DisplayName("Book: getIsbn returns correct ISBN")
    void testBookGetIsbn() {
        Book book = new Book("T1", "A1", "ISBN123");
        assertEquals("ISBN123", book.getIsbn());
    }

    @Test
    @DisplayName("Book: getIsbnOrId returns ISBN")
    void testBookGetIsbnOrId() {
        Book book = new Book("T1", "A1", "XX99");
        assertEquals("XX99", book.getIsbnOrId());
    }




    @Test
    @DisplayName("CD: getTitle returns initial title")
    void testCDGetTitle() {
        CD cd = new CD("C1", "A1", "I1");
        assertEquals("C1", cd.getTitle());
    }

    @Test
    @DisplayName("CD: setTitle updates title")
    void testCDSetTitle() {
        CD cd = new CD("Old", "A1", "I1");
        cd.setTitle("New");
        assertEquals("New", cd.getTitle());
    }

    @Test
    @DisplayName("CD: getAuthor returns initial author")
    void testCDGetAuthor() {
        CD cd = new CD("C1", "A1", "I1");
        assertEquals("A1", cd.getAuthor());
    }

    @Test
    @DisplayName("CD: setAuthor updates author")
    void testCDSetAuthor() {
        CD cd = new CD("C1", "Old", "I1");
        cd.setAuthor("New");
        assertEquals("New", cd.getAuthor());
    }

    @Test
    @DisplayName("CD: isAvailable returns default true")
    void testCDGetAvailability() {
        CD cd = new CD("C1", "A1", "I1");
        assertTrue(cd.isAvailable());
    }

    @Test
    @DisplayName("CD: setAvailable updates availability")
    void testCDSetAvailability() {
        CD cd = new CD("C1", "A1", "I1");
        cd.setAvailable(false);
        assertFalse(cd.isAvailable());
    }

    @Test
    @DisplayName("CD: getIsbn returns correct ISBN")
    void testCDGetIsbn() {
        CD cd = new CD("C1", "A1", "CD555");
        assertEquals("CD555", cd.getIsbn());
    }

    @Test
    @DisplayName("CD: getIsbnOrId returns ISBN")
    void testCDGetIsbnOrId() {
        CD cd = new CD("C1", "A1", "XYZ111");
        assertEquals("XYZ111", cd.getIsbnOrId());
    }




    @Test
    @DisplayName("MediaItem: getIsbnOrId returns UNKNOWN for non-Book/CD")
    void testUnknownIsbnOrId() {
        MediaItem item = new MediaItem("T", "A") {
            @Override public int getBorrowingPeriodDays() { return 0; }
            @Override public int getFinePerDay() { return 0; }
        };
        assertTrue(item.getIsbnOrId().startsWith("UNKNOWN-"));
    }



    @Test
    @DisplayName("MediaItem: toString prints correct format")
    void testMediaItemToString() {
        MediaItem item = new MediaItem("T", "A") {
            @Override public int getBorrowingPeriodDays() { return 0; }
            @Override public int getFinePerDay() { return 0; }
        };

        String expected = ": T by A [Available]";
        assertEquals(expected, item.toString());
    }

}
