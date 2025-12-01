package Domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CD_Test {

    private CD cd1;
    private CD cd2;

    @Before
    public void setUp() {
        // Creating test CDs
        cd1 = new CD("The Dark Side of the Moon", "Pink Floyd", "1234567890");
        cd2 = new CD("Abbey Road", "The Beatles", "0987654321");
    }

    @Test
    public void testCDCreation() {
        // Verifying if the CD is correctly created
        assertEquals("The Dark Side of the Moon", cd1.getTitle());
        assertEquals("Pink Floyd", cd1.getAuthor());
        assertEquals("1234567890", cd1.getIsbn());
    }

    @Test
    public void testGetBorrowingPeriodDays() {
        // Checking if the borrowing period for a CD is 7 days
        assertEquals(7, cd1.getBorrowingPeriodDays());
        assertEquals(7, cd2.getBorrowingPeriodDays());
    }

    @Test
    public void testGetFinePerDay() {
        // Checking if the fine per day for a CD is 20
        assertEquals(20, cd1.getFinePerDay());
        assertEquals(20, cd2.getFinePerDay());
    }

    @Test
    public void testToString() {
        // Checking the string representation of the CD
        assertTrue(cd1.toString().contains("CD: The Dark Side of the Moon"));
        assertTrue(cd2.toString().contains("CD: Abbey Road"));
        assertTrue(cd1.toString().contains("ISBN: 1234567890"));
        assertTrue(cd2.toString().contains("ISBN: 0987654321"));
    }

    @Test
    public void testAvailability() {
        // Assuming the isAvailable() method is inherited from MediaItem and works
        assertTrue(cd1.toString().contains("Available"));
        assertTrue(cd2.toString().contains("Available"));
        cd2.setAvailable(false);
        assertTrue(cd2.toString().contains("Borrowed"));
    }
}
