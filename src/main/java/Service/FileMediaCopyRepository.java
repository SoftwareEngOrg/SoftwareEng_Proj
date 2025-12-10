package Service;

import Domain.MediaCopy;
import Domain.MediaItem;
import java.io.*;
import java.util.*;
/**
 * The FileMediaCopyRepository class is responsible for managing media copies (books and CDs)
 * in a file-based repository. It handles adding new copies, checking availability, and persisting
 * data to a file. Each media copy is associated with a unique copy ID and availability status.
 */
public class FileMediaCopyRepository {

    private static FileMediaCopyRepository instance;
    private static final String FILE_PATH = "media_copies.txt";
    public static String repoPath = FILE_PATH;
    private List<MediaCopy> copies = new ArrayList<>();

    /**
     * Private constructor to load media copies from the file on initialization.
     */
    private FileMediaCopyRepository() {
        loadFromFile();
    }
    /**
     * Singleton pattern to get the single instance of FileMediaCopyRepository.
     * Ensures only one instance of this repository exists.
     *
     * @return the instance of the FileMediaCopyRepository
     */
    public static synchronized FileMediaCopyRepository getInstance() {
        if (instance == null) {
            instance = new FileMediaCopyRepository();

        }

        return instance;
    }

    /**
     * Returns the file path for storing media copies data.
     * If the `repoPath` is not set, it defaults to the standard file path.
     *
     * @return the file path as a string
     */
    private String getFilePath() {
        return (repoPath != null && !repoPath.isEmpty()) ? repoPath : FILE_PATH;
    }
    /**
     * Generates a unique copy ID based on the media item's ISBN and a sequential index.
     *
     * @param isbn the ISBN of the media item
     * @param index the index of the copy
     * @return the generated copy ID as a string
     */
    private String generateCopyId(String isbn, int index) {
        return isbn + "-" + index;
    }

    /**
     * Gets the highest copy index for a given media item's ISBN.
     * Used to create new unique copy IDs.
     *
     * @param isbn the ISBN of the media item
     * @return the highest index found for copies of the given ISBN
     */
    private int getMaxCopyIndexForIsbn(String isbn) {
        int max = 0;
        for (MediaCopy c : copies) {
            if (c.getMediaItem() != null && isbn.equalsIgnoreCase(c.getMediaItem().getIsbnOrId())) {

                String copyId = c.getCopyId();
                String[] parts = copyId.split("-");
                if (parts.length > 1) {
                    try {
                        int idx = Integer.parseInt(parts[parts.length - 1]);
                        if (idx > max) max = idx;
                    } catch (NumberFormatException _) {  }
                }
            }
        }
        return max;
    }
    /**
     * Adds a specified number of copies for a media item, initializing their availability.
     * Each new copy gets a unique copy ID based on the media item's ISBN and an index.
     *
     * @param mediaIsbn the ISBN of the media item
     * @param numberOfCopies the number of copies to add
     * @param available the availability status of the copies
     */
    public void addCopiesByBookIsbn(String mediaIsbn, int numberOfCopies, boolean available) {

        if (numberOfCopies <= 0) return;

        MediaItem item = findMediaItem( mediaIsbn);

        if (item == null) {
            System.out.println("Cannot find media item with ISBN: " + mediaIsbn);
            return;
        }

        int startIndex = getMaxCopyIndexForIsbn(mediaIsbn) + 1;

        for (int i = 0; i < numberOfCopies; i++) {
            String copyId = generateCopyId(mediaIsbn, startIndex + i);
            MediaCopy copy = new MediaCopy(copyId, item);
            copy.setAvailable(available);
            copies.add(copy);
        }

        saveToFile();
    }
    /**
     * Gets the count of available copies for a specific media item (based on ISBN).
     *
     * @param isbn the ISBN of the media item
     * @return the count of available copies
     */
    public int getAvailableCopiesCount(String isbn) {
        int count = 0;
        for (MediaCopy copy : copies) {
            if (copy.getMediaItem().getIsbnOrId().equals(isbn) && copy.isAvailable()) {
                count++;
            }
        }
        return count;
    }
    /**
     * Loads media copy data from a file and populates the list of copies.
     * Each line in the file represents a media copy with a copy ID, ISBN, and availability status.
     */
    private void loadFromFile() {
        copies.clear();

        File file = new File(getFilePath());
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");

                if (p.length == 3) {
                    String copyId = p[0];
                    String isbn = p[1];
                    boolean available = Boolean.parseBoolean(p[2]);

                    MediaItem item = findMediaItem(isbn);
                    if (item != null) {
                        MediaCopy copy = new MediaCopy(copyId, item);
                        copy.setAvailable(available);
                        copies.add(copy);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error loading media copies.");
        }
    }
    /**
     * Saves all the media copies to the file, overwriting the existing content.
     */
    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getFilePath()))) {
            for (MediaCopy c : copies) {
                pw.println(c.getCopyId() + ";" + c.getMediaItem().getIsbnOrId() + ";" + c.isAvailable());
            }
        } catch (Exception e) {
            System.out.println("Error saving media copies.");
        }
    }
    /**
     * Finds the media item (book or CD) associated with a specific ISBN.
     * It first checks the books repository, and if the item is not found, checks the CDs repository.
     *
     * @param isbn the ISBN of the media item
     * @return the MediaItem if found, or null if not found
     */
    public MediaItem findMediaItem(String isbn) {

            MediaItem book = FileBookRepository.getInstance().findByIsbn(isbn);
            if (book != null) return book;
            return FileCDRepository.getInstance().findByIsbn(isbn);
    }
    /**
     * Retrieves all media copies associated with a given ISBN.
     *
     * @param isbn the ISBN of the media item
     * @return a list of MediaCopy objects for the specified ISBN
     */
    public List<MediaCopy> getCopiesByIsbn(String isbn) {
        List<MediaCopy> result = new ArrayList<>();
        for (MediaCopy c : copies) {
            if (c.getMediaItem().getIsbnOrId().equals(isbn)) {
                result.add(c);
            }
        }
        return result;
    }

}
