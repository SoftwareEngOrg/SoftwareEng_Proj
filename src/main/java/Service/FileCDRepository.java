package Service;

import Domain.CD;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing CDs stored in a file.
 * Provides methods to save, update, query, and manage availability of CDs.
 */
public class FileCDRepository {

    private static final String FILE_PATH = "CD.txt";
    public static String repoPath = FILE_PATH;
    private static FileCDRepository instance;

    /**
     * Returns the singleton instance of FileCDRepository.
     *
     * @return the repository instance
     */
    public static FileCDRepository getInstance() {
        if (instance == null) {
            instance = new FileCDRepository();
        }
        return instance;
    }

    /**
     * Resets the singleton instance.
     */
    public static void reset() {
        instance = null;
    }

    /**
     * Returns the file path for the repository.
     *
     * @return the file path
     */
    private String getFilePath() {
        return (repoPath != null && !repoPath.isEmpty()) ? repoPath : FILE_PATH;
    }

    /**
     * Saves a CD to the file and adds its copies to the media copy repository.
     *
     * @param cd the CD to save
     * @param numberOfCopies number of copies to add
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
     * Returns a list of all CDs in the repository.
     *
     * @return list of CDs
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
     * Updates all CDs in the repository by overwriting the file.
     *
     * @param cds the list of CDs to write
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
     * @param isbn the ISBN to search for
     * @return the CD if found, otherwise null
     */
    public CD findByIsbn(String isbn) {
        return findAllCDs().stream()
                .filter(cd -> cd.getIsbn().equalsIgnoreCase(isbn))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates the availability status of a CD based on available copies.
     *
     * @param isbn the ISBN of the CD
     */
    public void updateCDAvailability(String isbn) {
        CD cd = findByIsbn(isbn);
        if (cd != null) {
            int availableCopies = FileMediaCopyRepository.getInstance().getAvailableCopiesCount(isbn);
            boolean nowAvailable = (availableCopies > 0);
            cd.setAvailable(nowAvailable);

            List<CD> cds = findAllCDs();
            for (int i = 0; i < cds.size(); i++) {
                if (cds.get(i).getIsbn().equalsIgnoreCase(isbn)) {
                    cds.set(i, cd);
                    break;
                }
            }

            updateAll(cds);
        }
    }

    /**
     * Updates or adds a CD in the repository.
     *
     * @param cd the CD to update or add
     */
    public void updateCD(CD cd) {
        if (cd == null) return;
        List<CD> cds = findAllCDs();
        boolean replaced = false;
        for (int i = 0; i < cds.size(); i++) {
            if (cds.get(i).getIsbn() != null && cds.get(i).getIsbn().equalsIgnoreCase(cd.getIsbn())) {
                cds.set(i, cd);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            cds.add(cd);
        }
        updateAll(cds);
    }
}
