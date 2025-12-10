package Domain;

public class MediaCopy {

    private String copyId;
    private MediaItem mediaItem;
    private boolean available;



    public MediaCopy( String copyId , MediaItem mediaItem  )
    {
        this.copyId = copyId;
        this.mediaItem = mediaItem;
        this.available = true;

    }


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
