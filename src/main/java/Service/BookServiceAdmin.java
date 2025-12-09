package Service;

import Domain.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BookServiceAdmin extends BookService {
    private final FileLoanRepository loanFile = new FileLoanRepository();
    private final FileUserRepository userFile = new FileUserRepository();
    private final FileCDRepository fileCD = FileCDRepository.getInstance();



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
