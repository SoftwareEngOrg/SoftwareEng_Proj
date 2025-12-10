package Service;

import Domain.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileLoanRepository {
    static FileLoanRepository instance;
    public static  String FILE_PATH = "loans.txt";
    public static String repoPath = FILE_PATH;
    private final List<Loan> loans = new ArrayList<>();
    private final FileBookRepository bookRepository; // Reuse cached books
    private final FileCDRepository cdRepository;

    private String getFilePath() {
        return (repoPath != null && !repoPath.isEmpty()) ? repoPath : FILE_PATH;
    }

    public static void setRepoPath (String newPath) {
        repoPath = newPath;
        instance = null;
    }



    public static synchronized FileLoanRepository getInstance() {
        if (instance == null) {
            instance = new FileLoanRepository();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }



    public FileLoanRepository() {
        this.bookRepository = FileBookRepository.getInstance();
        this.cdRepository = FileCDRepository.getInstance();
        loadLoans();
    }



    public synchronized Loan borrowItem(User user, MediaItem item) {
        if (user == null) throw new IllegalArgumentException("user is null");
        if (item == null) throw new IllegalArgumentException("item is null");
        if (!item.isAvailable()) throw new IllegalStateException("Item is not available");


        item.setAvailable(false);


        if (item instanceof Book) {
            FileBookRepository.getInstance().updateBooks((Book) item);
        }

        else if (item instanceof CD) {
            FileCDRepository.getInstance().updateCD((CD) item);
        }


        Loan loan = new Loan(UUID.randomUUID().toString(), user, item, LocalDate.now());
        this.loans.add(loan);
        saveToFile();
        return loan;
    }


    public synchronized boolean returnItem(String loanId, LocalDate returnDate) {
        Loan loan = findLoanById(loanId);
        if (loan == null) return false;
        if (loan.getReturnDate() != null) return false;

        loan.returnItem(returnDate != null ? returnDate : LocalDate.now());

        MediaItem item = loan.getMediaItem();

        item.setAvailable(true);
        if (item instanceof Book) {
            FileBookRepository.getInstance().updateBooks((Book) item);
        } else if (item instanceof CD) {
            FileCDRepository.getInstance().updateCD((CD) item);
        }

        saveToFile();
        return true;
    }




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

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getFilePath()))) {
            for (Loan loan : loans) {
                String returnDateStr = loan.getReturnDate() == null ? "NULL" : loan.getReturnDate().toString();
                String line = String.format("%s;%s;%s;%s;%s",
                        loan.getLoanId(),
                        loan.getUser().getUsername(),
                        loan.getMediaItem().getIsbnOrId(),
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
        File file = new File(getFilePath());
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length != 5) continue;

                String loanId = p[0];
                String username = p[1];
                String itemId = p[2];
                LocalDate borrowDate = LocalDate.parse(p[3]);
                LocalDate returnDate = p[4].equals("NULL") ? null : LocalDate.parse(p[4]);


                User user = findUserByUsername(username);
                MediaItem item = findMediaItemById(itemId);

                if (item != null) {
                    Loan loan = new Loan(loanId, user, item, borrowDate);
                    if (returnDate != null) {
                        loan.returnItem(returnDate);
                    } else {
                        item.setAvailable(false);
                    }
                    loans.add(loan);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading loans: " + e.getMessage());
        }
    }

    private User findUserByUsername(String username) {

        return new User(username, "temp", username.equals("admin") ? "admin" : "customer");
    }

    private MediaItem findMediaItemById(String id) {
        MediaItem book = bookRepository.findAllBooks().stream()
                .filter(b -> b.getIsbn().equals(id))
                .findFirst()
                .orElse(null);

        if (book != null) {
            return book;
        }


        return cdRepository.findAllCDs().stream()
                .filter(cd -> cd.getIsbn().equals(id))
                .findFirst()
                .orElse(null);
    }

}
