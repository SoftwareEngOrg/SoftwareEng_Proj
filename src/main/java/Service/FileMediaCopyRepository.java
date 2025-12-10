package Service;

import Domain.MediaCopy;
import Domain.MediaItem;
import java.io.*;
import java.util.*;

public class FileMediaCopyRepository {

    private static FileMediaCopyRepository instance;
    private static final String FILE_PATH = "media_copies.txt";
    public static String repoPath = FILE_PATH;
    private List<MediaCopy> copies = new ArrayList<>();


    private String getFilePath() {
        return (repoPath != null && !repoPath.isEmpty()) ? repoPath : FILE_PATH;
    }

    public static void setRepoPath (String newPath) {
        repoPath = newPath;
        instance = null;
    }

    public static synchronized FileMediaCopyRepository getInstance() {
        if (instance == null) {
            instance = new FileMediaCopyRepository();

        }

        return instance;
    }


    public static void reset() {
        instance = null;
    }


    private FileMediaCopyRepository() {
        loadFromFile();
    }

    private String generateCopyId(String isbn, int index) {
        return isbn + "-" + index;
    }

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

    public int getAvailableCopiesCount(String isbn) {
        int count = 0;
        for (MediaCopy copy : copies) {
            if (copy.getMediaItem().getIsbnOrId().equals(isbn) && copy.isAvailable()) {
                count++;
            }
        }
        return count;
    }

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

    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getFilePath()))) {
            for (MediaCopy c : copies) {
                pw.println(c.getCopyId() + ";" + c.getMediaItem().getIsbnOrId() + ";" + c.isAvailable());
            }
        } catch (Exception e) {
            System.out.println("Error saving media copies.");
        }
    }

    public MediaItem findMediaItem(String isbn) {

            MediaItem book = FileBookRepository.getInstance().findByIsbn(isbn);
            if (book != null) return book;
            return FileCDRepository.getInstance().findByIsbn(isbn);
    }

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
