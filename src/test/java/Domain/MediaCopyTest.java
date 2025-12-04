package Domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MediaCopyTest {

    private MediaItem sampleItem() {
        return new Book("T1", "A1", "ISBN1");
    }

    // ========== Constructor Tests ==========

    @Test
    @DisplayName("MediaCopy: constructor sets fields and default availability=true")
    void testConstructorDefaultAvailability() {
        MediaCopy copy = new MediaCopy("C1", sampleItem());
        assertTrue(copy.isAvailable());
    }

    @Test
    @DisplayName("MediaCopy: constructor sets custom availability")
    void testConstructorCustomAvailability() {
        MediaCopy copy = new MediaCopy("C1", sampleItem(), false);
        assertFalse(copy.isAvailable());
    }


    // ========== Getter & Setter: copyId ==========

    @Test
    @DisplayName("MediaCopy: getCopyId returns initial value")
    void testGetCopyId() {
        MediaCopy copy = new MediaCopy("C1", sampleItem());
        assertEquals("C1", copy.getCopyId());
    }

    @Test
    @DisplayName("MediaCopy: setCopyId updates value")
    void testSetCopyId() {
        MediaCopy copy = new MediaCopy("OLD", sampleItem());
        copy.setCopyId("NEW");
        assertEquals("NEW", copy.getCopyId());
    }


    // ========== Getter & Setter: mediaItem ==========

    @Test
    @DisplayName("MediaCopy: getMediaItem returns correct MediaItem")
    void testGetMediaItem() {
        MediaItem item = sampleItem();
        MediaCopy copy = new MediaCopy("C1", item);
        assertEquals(item, copy.getMediaItem());
    }

    @Test
    @DisplayName("MediaCopy: setMediaItem updates MediaItem")
    void testSetMediaItem() {
        MediaItem item1 = sampleItem();
        MediaItem item2 = new Book("NewT", "NewA", "ISBN2");

        MediaCopy copy = new MediaCopy("C1", item1);
        copy.setMediaItem(item2);

        assertEquals(item2, copy.getMediaItem());
    }


    // ========== Getter & Setter: available ==========

    @Test
    @DisplayName("MediaCopy: isAvailable returns correct value")
    void testGetAvailable() {
        MediaCopy copy = new MediaCopy("C1", sampleItem(), true);
        assertTrue(copy.isAvailable());
    }

    @Test
    @DisplayName("MediaCopy: setAvailable updates availability")
    void testSetAvailable() {
        MediaCopy copy = new MediaCopy("C1", sampleItem());
        copy.setAvailable(false);
        assertFalse(copy.isAvailable());
    }


    // ========== toString Test ==========

    @Test
    @DisplayName("MediaCopy: toString contains correct formatted data")
    void testToString() {
        MediaCopy copy = new MediaCopy("C1", sampleItem(), true);
        String result = copy.toString();

        assertTrue(result.contains("Copy[C1]"));
    }
}
