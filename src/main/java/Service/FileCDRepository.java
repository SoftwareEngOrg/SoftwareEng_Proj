// File: FileCDRepository.java
package Service;

import Domain.CD;
import Domain.MediaItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileCDRepository {
    private static final String FILE_PATH = "CD.txt";
    private static FileCDRepository instance;

    public static FileCDRepository getInstance() {
        if (instance == null) {
            instance = new FileCDRepository();
        }
        return instance;
    }

    public void saveCD(CD cd, int numberOfCopies) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH, true))) {

            pw.println(cd.getTitle() + ";" + cd.getAuthor() + ";" + cd.getIsbn() + ";" + true);

        } catch (Exception e) {
            System.out.println("Error writing to books file: " + e.getMessage());
        }

        FileMediaCopyRepository.getInstance().addCopiesByBookIsbn( cd.getIsbn(), numberOfCopies, true);
    }

    public List<CD> findAllCDs() {
        List<CD> cds = new ArrayList<>();
        File file = new File(FILE_PATH);
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

    public void updateAll(List<CD> cds) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (CD cd : cds) {
                writer.write(cd.getTitle() + ";" + cd.getAuthor() + ";" + cd.getIsbn() + ";" + cd.isAvailable());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating CD file");
        }
    }

    public CD findByIsbn(String isbn) {
        return findAllCDs().stream()
                .filter(cd -> cd.getIsbn().equalsIgnoreCase(isbn))
                .findFirst()
                .orElse(null);
    }

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