package Domain;

public class MediaCopy {

    private String copyId;
    private MediaItem mediaItem;
    private boolean available;
    /**
     * Creates a new MediaCopy with the specified copy ID and media item.
     * The media copy is available by default.
     *
     * @param copyId the ID of the media copy
     * @param mediaItem the media item associated with this copy
     */


    public MediaCopy( String copyId , MediaItem mediaItem  )
    {
        this.copyId = copyId;
        this.mediaItem = mediaItem;
        this.available = true;

    }
    /**
     * Creates a new MediaCopy with the specified copy ID, media item, and availability.
     *
     * @param copyId the ID of the media copy
     * @param mediaItem the media item associated with this copy
     * @param available the availability status of the media copy
     */

    public MediaCopy( String copyId , MediaItem mediaItem , boolean available )
    {
        this.copyId = copyId;
        this.mediaItem = mediaItem;
        this.available = available;

    }


    public String getCopyId() {
        return copyId;
    }

    public void setCopyId(String copyId) {
        this.copyId = copyId;
    }

    public MediaItem getMediaItem() {
        return mediaItem;
    }

    public void setMediaItem(MediaItem mediaItem) {
        this.mediaItem = mediaItem;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Returns a string representation of the media copy.
     *
     * @return details of the media copy
     */

    @Override
    public String toString() {
        return String.format("Copy[%s]: %s - %s [%s] ",
                copyId,
                mediaItem.getTitle(),
                mediaItem.getAuthor(),
                available ? "Available" : "Borrowed"
               );
    }
}
