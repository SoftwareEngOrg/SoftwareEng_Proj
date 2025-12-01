package Service;

import Domain.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BookServiceAdmin extends BookService {
    private FileLoanRepository loanFile = new FileLoanRepository();
    private FileUserRepository userFile = new FileUserRepository();
    private FileCDRepository fileCD = FileCDRepository.getInstance();

    public boolean addBook(Book book) {
        List<Book> existingBooks = this.fileBook.findAllBooks();

        boolean exists = existingBooks.stream()
                .anyMatch(b -> b.getIsbn().equalsIgnoreCase(book.getIsbn()));
        if (fileCD.findByIsbn(book.getIsbn()) != null) {
            return false; // already exists
        }
        if (exists) {
            return false;
        }
        this.fileBook.saveBook(book);
        return true;
    }

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

    public void unregisterAllUsers(List<User> inActiveUsers) {
        userFile.unregisterAllUsers(inActiveUsers);
    }

    public void unregisterUserByUsername(String username) {
        if(userFile.unregisterUserByUsername(username)){
            System.out.println("user has been Unregistered successfully");
        }else
            System.out.println("something went wrong");
    }

    public boolean addCD(CD cd) {
        List<Book> existingBooks = this.fileBook.findAllBooks();

        boolean exists = existingBooks.stream()
                .anyMatch(b -> b.getIsbn().equalsIgnoreCase(cd.getIsbn()));

        if (exists) {
            return false;
        }
        if (fileCD.findByIsbn(cd.getIsbn()) != null) {
            return false; // already exists
        }
        fileCD.saveCD(cd);
        return true;
    }




}
