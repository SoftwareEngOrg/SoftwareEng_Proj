// File: FileCDRepository.java
package Service;

import Domain.CD;
import Domain.MediaItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The FileCDRepository class is responsible for managing the persistence of CD objects
 * in a text file. It supports operations such as saving new CDs, updating CD availability,
 * and searching for CDs by ISBN.
 */
public class FileCDRepository {
    private static final String FILE_PATH = "CD.txt";
    public static String repoPath = FILE_PATH;
    private static FileCDRepository instance;
    /**
     * Returns the instance of the FileCDRepository. Implements Singleton Pattern
     * to ensure only one instance of this repository is created.
     *
     * @return the single instance of the FileCDRepository
     */
    public static FileCDRepository getInstance() {
        if (instance == null) {
            instance = new FileCDRepository();
        }
        return instance;
    }

    /**
     * Returns the file path where the CDs are stored.
     *
     * @return the file path as a string
     */
    private String getFilePath() {
        return (repoPath != null && !repoPath.isEmpty()) ? repoPath : FILE_PATH;
    }

    /**
     * Saves a new CD to the repository and adds copies to the media copy repository.
     *
     * @param cd the CD to be saved
     * @param numberOfCopies the number of copies of the CD to be added
     */
    public static void saveCD(CD cd, int numberOfCopies) {
        FileCDRepository repo = getInstance();
        try (PrintWriter pw = new PrintWriter(new FileWriter(repo.getFilePath(), true))) {

            pw.println(cd.getTitle() + ";" + cd.getAuthor() + ";" + cd.getIsbn() + ";" + true);

        } catch (Exception e) {
            System.out.println("Error writing to CDs file: " + e.getMessage());
        }

        FileMediaCopyRepository.getInstance().addCopiesByBookIsbn(cd.getIsbn(), numberOfCopies, true);
    }
    /**
     * Loads all CDs from the file and returns them as a list.
     *
     * @return a list of all CDs in the repository
     */
    public List<CD> findAllCDs() {
        List<CD> cds = new ArrayList<>();
        File file = new File(getFilePath());
        if (!file.exists()) return cds;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    CD cd = new CD(parts[0], parts[1], parts[2]);
                    cd.setAvailable(Boolean.parseBoolean(parts[3]));
                    cds.add(cd);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CDs: " + e.getMessage());
        }
        return cds;
    }
    /**
     * Updates the availability status of all CDs and saves the changes to the file.
     *
     * @param cds the list of CDs to be updated
     */
    public void updateAll(List<CD> cds) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFilePath()))) {
            for (CD cd : cds) {
                writer.write(cd.getTitle() + ";" + cd.getAuthor() + ";" + cd.getIsbn() + ";" + cd.isAvailable());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating CD file");
        }
    }
    /**
     * Finds a CD by its ISBN.
     *
     * @param isbn the ISBN of the CD
     * @return the CD if found, or null if not found
     */
    public CD findByIsbn(String isbn) {
        return findAllCDs().stream()
                .filter(cd -> cd.getIsbn().equalsIgnoreCase(isbn))
                .findFirst()
                .orElse(null);
    }
    /**
     * Updates the availability status of a CD based on the available copies in the media copy repository.
     * If the CD becomes available, it will notify the waitlist.
     *
     * @param isbn the ISBN of the CD to update
     */
    public void updateCDAvailability(String isbn) {
        CD cd = findByIsbn(isbn);
        if (cd != null) {

            int availableCopies = FileMediaCopyRepository.getInstance()
                    .getAvailableCopiesCount(isbn);

            boolean wasAvailable = cd.isAvailable();
            boolean nowAvailable = (availableCopies > 0);

            cd.setAvailable(nowAvailable);

            updateAll(findAllCDs());

            if (!wasAvailable && nowAvailable) {
                System.out.println("CD is now available - notifying waitlist...");
            }
        }
    }
}