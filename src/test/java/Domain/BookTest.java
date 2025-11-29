// src/test/java/Domain/BookTest.java
package Domain;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    @DisplayName("Book constructor sets title, author, isbn correctly")
    void constructor_setsAllFields() {
        Book book = new Book("1984", "George Orwell", "999");

        assertEquals("1984", book.getTitle());
        assertEquals("George Orwell", book.getAuthor());
        assertEquals("999", book.getIsbn());
        assertTrue(book.isAvailable());
    }

    @Test
    @DisplayName("getIsbn returns correct ISBN")
    void getIsbn_returnsCorrectValue() {
        Book book = new Book("Title", "Author", "ISBN-12345");
        assertEquals("ISBN-12345", book.getIsbn());
    }

    @Test
    @DisplayName("getBorrowingPeriodDays returns 28")
    void getBorrowingPeriodDays_returns28() {
        Book book = new Book("Any", "Any", "1");
        assertEquals(28, book.getBorrowingPeriodDays());
    }

    @Test
    @DisplayName("getFinePerDay returns 10")
    void getFinePerDay_returns10() {
        Book book = new Book("Any", "Any", "1");
        assertEquals(10, book.getFinePerDay());
    }

    @Test
    @DisplayName("toString contains Title, Author, Isbn and availability")
    void toString_containsAllFields() {
        Book book = new Book("Harry Potter", "J.K. Rowling", "777");
        book.setAvailable(false);

        String str = book.toString();

        assertTrue(str.contains("Title:Harry Potter"));
        assertTrue(str.contains("Author:J.K. Rowling"));
        assertTrue(str.contains("Isbn:777"));
        assertTrue(str.contains("false"));
    }

    @Test
    @DisplayName("Book inherits availability from MediaItem correctly")
    void book_startsAvailable_andCanBeChanged() {
        Book book = new Book("Test", "Test", "111");
        assertTrue(book.isAvailable());

        book.setAvailable(false);
        assertFalse(book.isAvailable());
    }
}