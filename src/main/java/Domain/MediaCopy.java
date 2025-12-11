package Domain;

/**
 * Represents a physical copy of a media item in the library system.
 * Each copy has its own unique identifier and availability status.
 *
 * <p>Example usage:</p>
 * <pre><code>
 * MediaCopy copy = new MediaCopy("C001", mediaItem);
 * boolean free = copy.isAvailable();
 * </code></pre>
 *
 * @since 1.0
 */
public class MediaCopy {

    private String copyId;
    private MediaItem mediaItem;
    private boolean available;

    /**
     * Creates a new MediaCopy and marks it as available by default.
     *
     * @param copyId     the unique identifier of this copy
     * @param mediaItem  the media item that this copy represents
     */
    public MediaCopy(String copyId, MediaItem mediaItem) {
        this.copyId = copyId;
        this.mediaItem = mediaItem;
        this.available = true;
    }

    /**
     * Creates a new MediaCopy with a specified availability status.
     *
     * @param copyId     the unique identifier of this copy
     * @param mediaItem  the media item that this copy represents
     * @param available  the initial availability status
     */
    public MediaCopy(String copyId, MediaItem mediaItem, boolean available) {
        this.copyId = copyId;
        this.mediaItem = mediaItem;
        this.available = available;
    }

    /**
     * Returns the unique ID of this media copy.
     *
     * @return the copy ID
     */
    public String getCopyId() {
        return copyId;
    }

    /**
     * Sets the unique ID for this media copy.
     *
     * @param copyId the new identifier
     */
    public void setCopyId(String copyId) {
        this.copyId = copyId;
    }

    /**
     * Returns the media item associated with this copy.
     *
     * @return the media item
     */
    public MediaItem getMediaItem() {
        return mediaItem;
    }

    /**
     * Assigns a new media item to this copy.
     *
     * @param mediaItem the media item to associate
     */
    public void setMediaItem(MediaItem mediaItem) {
        this.mediaItem = mediaItem;
    }

    /**
     * Checks whether this copy is currently available.
     *
     * @return true if available, otherwise false
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets the availability status of this copy.
     *
     * @param available true if the item is available, false if borrowed
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Returns a formatted string describing this media copy.
     *
     * @return a string representation of the copy, including ID, title, author, and availability
     */
    @Override
    public String toString() {
        return String.format(
                "Copy[%s]: %s - %s [%s] ",
                copyId,
                mediaItem.getTitle(),
                mediaItem.getAuthor(),
                available ? "Available" : "Borrowed"
        );
    }
}
