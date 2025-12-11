package Service;

import Domain.MediaCopy;
import Domain.MediaItem;
import java.io.*;
import java.util.*;

/**
 * Repository class for managing media copies (books, CDs) stored in a file.
 * Provides methods to add copies, query availability, and persist changes.
 */
public class FileMediaCopyRepository {

    private static FileMediaCopyRepository instance;
    private static final String FILE_PATH = "media_copies.txt";
    public static String repoPath = FILE_PATH;
    private List<MediaCopy> copies = new ArrayList<>();

    /**
     * Returns the file path for the repository.
     *
     * @return the file path
     */
    private String getFilePath() {
        return (repoPath != null && !repoPath.isEmpty()) ? repoPath : FILE_PATH;
    }

    /**
     * Sets a new file path for the repository and resets the instance.
     *
     * @param newPath the new file path
     */
    public static void setRepoPath(String newPath) {
        repoPath = newPath;
        instance = null;
    }

    /**
     * Returns the singleton instance of FileMediaCopyRepository.
     *
     * @return the repository instance
     */
    public static synchronized FileMediaCopyRepository getInstance() {
        if (instance == null) {
            instance = new FileMediaCopyRepository();
        }
        return instance;
    }

    /**
     * Resets the singleton instance.
     */
    public static void reset() {
        instance = null;
    }

    private FileMediaCopyRepository() {
        loadFromFile();
    }

    /**
     * Adds multiple copies of a media item by ISBN.
     *
     * @param mediaIsbn the ISBN of the media item
     * @param numberOfCopies the number of copies to add
     * @param available whether the copies are initially available
     */
    public void addCopiesByBookIsbn(String mediaIsbn, int numberOfCopies, boolean available) {
        if (numberOfCopies <= 0) return;

        MediaItem item = findMediaItem(mediaIsbn);
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
     * Returns the number of available copies for a given ISBN.
     *
     * @param isbn the ISBN to check
     * @return the number of available copies
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
     * Returns a list of copies for a given ISBN.
     *
     * @param isbn the ISBN to search for
     * @return list of media copies
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

    /**
     * Finds a media item by ISBN in book or CD repositories.
     *
     * @param isbn the ISBN to search for
     * @return the media item if found, otherwise null
     */
    public MediaItem findMediaItem(String isbn) {
        MediaItem book = FileBookRepository.getInstance().findByIsbn(isbn);
        if (book != null) return book;
        return FileCDRepository.getInstance().findByIsbn(isbn);
    }

    /**
     * Loads all media copies from the file into memory.
     */
    void loadFromFile() {
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
     * Saves all media copies from memory to the file.
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

    // Helper methods for copy ID generation and max index
    private String generateCopyId(String isbn, int index) {
        return isbn + "-" + index;
    }

    private int getMaxCopyIndexForIsbn(String isbn) {
        int max = 0;
        for (MediaCopy c : copies) {
            if (c.getMediaItem() != null && isbn.equalsIgnoreCase(c.getMediaItem().getIsbnOrId())) {
                String[] parts = c.getCopyId().split("-");
                if (parts.length > 1) {
                    try {
                        int idx = Integer.parseInt(parts[parts.length - 1]);
                        if (idx > max) max = idx;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return max;
    }
}
