package Service;

import Domain.Loan;
import Domain.MediaItem;
import Domain.User;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileLoanRepository {
    public static  String FILE_PATH = "loans.txt";
    private final List<Loan> loans = new ArrayList<>();
    private final FileBookRepository bookRepository; // Reuse cached books

    public FileLoanRepository() {
        this.bookRepository = FileBookRepository.getInstance();
        loadLoans();
    }


    public Loan borrowItem(User user, MediaItem item) {
        if (!item.isAvailable()) {
            throw new IllegalStateException("Item is not available for borrowing.");
        }

        String loanId = UUID.randomUUID().toString().substring(0, 8); // Simple unique ID
        Loan loan = new Loan(loanId, user, item, LocalDate.now());
        item.setAvailable(false); // Mark as borrowed
        bookRepository.updateBooks(item);                // ← PERSIST TO FILE!
        loans.add(loan);
        saveToFile();
        return loan;
    }

    public boolean returnItem(String loanId, LocalDate returnDate) {
        for (Loan loan : loans) {
            if (loan.getLoanId().equals(loanId) && loan.getReturnDate() == null) {
                loan.returnItem(returnDate != null ? returnDate : LocalDate.now());

                // Mark book as available and update the file
                MediaItem item = loan.getMediaItem();
                item.setAvailable(true);
                bookRepository.updateBooks(item);  // ← PERSIST TO FILE!

                saveToFile();
                return true;
            }
        }
        return false;
    }


    // === Queries ===
    public List<Loan> getActiveLoansForUser(String username)
    {
        return loans.stream()
                .filter(loan -> loan.getUser().getUsername().equals(username))
                .filter(loan -> loan.getReturnDate() == null)
                .toList();
    }

    public List<Loan> getAllActiveLoans() {
        return loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .toList();
    }

    public List<Loan> getOverdueLoans(LocalDate currentDate) {
        return loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .filter(loan -> loan.isOverdue(currentDate))
                .toList();
    }

    public Loan findLoanById(String loanId) {
        return loans.stream()
                .filter(l -> l.getLoanId().equals(loanId))
                .findFirst()
                .orElse(null);
    }

    // === Persistence ===

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Loan loan : loans) {
                String returnDateStr = loan.getReturnDate() == null ? "NULL" : loan.getReturnDate().toString();
                String line = String.format("%s;%s;%s;%s;%s",
                        loan.getLoanId(),
                        loan.getUser().getUsername(),
                        loan.getMediaItem().getIsbnOrId(), // We'll improve this later for non-books
                        loan.getBorrowDate(),
                        returnDateStr
                );
                pw.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error saving loans: " + e.getMessage());
        }
    }



    private void loadLoans()
    {
        loans.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length != 5) continue;

                String loanId = p[0];
                String username = p[1];
                String itemId = p[2]; // ISBN for Book, later ID for CD
                LocalDate borrowDate = LocalDate.parse(p[3]);
                LocalDate returnDate = p[4].equals("NULL") ? null : LocalDate.parse(p[4]);

                // Find user (simple lookup - improve later if needed)
                User user = findUserByUsername(username);
                MediaItem item = findMediaItemById(itemId);

                if (user != null && item != null) {
                    Loan loan = new Loan(loanId, user, item, borrowDate);
                    if (returnDate != null) {
                        loan.returnItem(returnDate);
                    } else {
                        item.setAvailable(false); // Still borrowed
                    }
                    loans.add(loan);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading loans: " + e.getMessage());
        }
    }



    // Helper: find user
    private User findUserByUsername(String username) {
        // Temporary: just create dummy user - replace with real repo later
        return new User(username, "temp", username.equals("admin") ? "admin" : "customer");
    }

    // find MediaItem by ISBN
    private MediaItem findMediaItemById(String id) {
        return bookRepository.findAllBooks().stream()
                .filter(b -> b.getIsbn().equals(id))
                .findFirst()
                .orElse(null);
    }

}
