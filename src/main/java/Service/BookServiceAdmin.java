package Service;

import Domain.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Admin service for managing books, CDs, and user operations.
 */
public class BookServiceAdmin extends BookService {
    private final FileLoanRepository loanFile = new FileLoanRepository();
    private final FileUserRepository userFile = new FileUserRepository();
    private final FileCDRepository fileCD = FileCDRepository.getInstance();


    /**
     * Adds a new book to the inventory with a specified number of copies.
     *
     * @param book          the book to be added
     * @param numberOfCopies the number of copies to add
     * @return true if book added successfully, false if already exists
     */
    public boolean addBook(Book book, int numberOfCopies) {
        if (numberOfCopies <= 0) return false;

        List<Book> existingBooks = this.fileBook.findAllBooks();
        boolean exists = existingBooks.stream()
                .anyMatch(b -> b.getIsbn().equalsIgnoreCase(book.getIsbn()));

        if (fileCD.findByIsbn(book.getIsbn()) != null) return false;
        if (exists) return false;


        FileBookRepository.getInstance();
        FileBookRepository.saveBook(book, numberOfCopies);

        return true;
    }

    /**
     * Returns a list of inactive users (users without active loans).
     *
     * @return list of inactive users
     */
    public List <User> viewInactiveUsers() {
        List <Loan> activeLoans = loanFile.getAllActiveLoans();
        HashSet<String> usersActive = new HashSet<>();
        for (Loan loan : activeLoans) {
            usersActive.add(loan.getUser().getUsername());
        }
        List <User> allUsers = userFile.getAllUsers();
        List<User> inactiveUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (!usersActive.contains(user.getUsername()) && user.getRole().equals("customer"))
                    inactiveUsers.add(user);
        }
        return inactiveUsers;
    }
    /**
     * Unregisters all inactive users.
     *
     * @param inActiveUsers list of inactive users to unregister
     */
    public void unregisterAllUsers(List<User> inActiveUsers) {
        userFile.unregisterAllUsers(inActiveUsers);
    }

    /**
     * Unregisters a specific user by username.
     *
     * @param username the username of the user to unregister
     */
    public void unregisterUserByUsername(String username) {
        if(userFile.unregisterUserByUsername(username)){
            System.out.println("user has been Unregistered successfully");
        }else
            System.out.println("something went wrong");
    }
    /**
     * Adds a new CD to the inventory with a specified number of copies.
     *
     * @param cd            the CD to be added
     * @param numberOfCopies the number of copies to add
     * @return true if CD added successfully, false if already exists
     */
    public boolean addCD(CD cd, int numberOfCopies) {
        if (numberOfCopies <= 0) return false;

        List<CD> existingCDs = this.fileCD.findAllCDs();
        boolean exists = existingCDs.stream()
                .anyMatch(b -> b.getIsbn().equalsIgnoreCase(cd.getIsbn()));

        if (fileBook.findByIsbn(cd.getIsbn()) != null) return false;
        if (exists) return false;


        FileCDRepository.getInstance();
        FileCDRepository.saveCD(cd, numberOfCopies);

        return true;
    }

}
