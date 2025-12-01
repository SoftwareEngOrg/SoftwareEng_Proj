// src/test/java/Domain/MediaItemTest.java
package Domain;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class MediaItemTest {

    private static class TestBook extends MediaItem {
        public TestBook(String title, String author) {
            super(title, author);
        }
        @Override public int getBorrowingPeriodDays() { return 14; }
        @Override public int getFinePerDay() { return 5; }
        public String getIsbn() { return "TEST123"; } // for getIsbnOrId test
    }

    private MediaItem item;

    @BeforeEach
    void setUp() {
        item = new TestBook("Java Guide", "Ahmed");
    }

    @Test
    @DisplayName("Constructor initializes title, author and sets available = true")
    void constructor_initializesCorrectly() {
        assertEquals("Java Guide", item.getTitle());
        assertEquals("Ahmed", item.getAuthor());
        assertTrue(item.isAvailable());
    }

    @Test
    @DisplayName("setAvailable and isAvailable work correctly")
    void setAvailable_changesAvailability() {
        item.setAvailable(false);
        assertFalse(item.isAvailable());

        item.setAvailable(true);
        assertTrue(item.isAvailable());
    }

    @Test
    @DisplayName("getIsbnOrId returns ISBN when instance is Book")
    void getIsbnOrId_whenBook_returnsIsbn() {
        Book realBook = new Book("Clean Code", "Robert", "978");
        assertEquals("978", realBook.getIsbnOrId());
    }
    @Test
    @DisplayName("getIsbnOrId returns ISBN when instance is Book")
    void getIsbnOrId_whenCD_returnsIsbn() {
        CD realBook = new CD("Clean Code", "Robert", "978");
        assertEquals("978", realBook.getIsbnOrId());
    }

    @Test
    @DisplayName("getIsbnOrId returns fallback when not Book")
    void getIsbnOrId_whenNotBook_returnsUnknown() {
        MediaItem cd = new MediaItem("Greatest Hits", "Band X") {
            @Override public int getBorrowingPeriodDays() { return 7; }
            @Override public int getFinePerDay() { return 20; }
        };

        String result = cd.getIsbnOrId();
        assertTrue(result.startsWith("UNKNOWN-"));
    }

    @Test
    @DisplayName("toString contains class name, title, author and availability")
    void toString_containsAllInfo() {
        String str = item.toString();
        assertTrue(str.contains("TestBook"));
        assertTrue(str.contains("Java Guide"));
        assertTrue(str.contains("Ahmed"));
        assertTrue(str.contains("Available"));
    }
    @Test
    @DisplayName("toString contains class name, title, author and availability but borrowed")
    void toString_containsAllInfo_Borrowed() {
        item.setAvailable(false);
        String str = item.toString();
        assertTrue(str.contains("TestBook"));
        assertTrue(str.contains("Java Guide"));
        assertTrue(str.contains("Ahmed"));
        assertTrue(str.contains("Borrowed"));
    }
}