package Service;

import Domain.Book;
import Domain.Loan;
import Domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BookServiceAdmin extends BookService {
    private FileLoanRepository loanFile = new FileLoanRepository();
    private FileUserRepository userFile = new FileUserRepository();
    public boolean addBook(Book book) {
        List<Book> existingBooks = this.fileBook.findAllBooks();

        boolean exists = existingBooks.stream()
                .anyMatch(b -> b.getIsbn().equalsIgnoreCase(book.getIsbn()));

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
}
