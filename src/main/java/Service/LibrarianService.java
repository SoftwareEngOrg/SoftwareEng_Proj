package Service;

import Domain.Loan;

import java.time.LocalDate;
import java.util.List;

/**
 * The LibrarianService class provides methods to manage and interact with active loans in the library system.
 * It allows librarians to view overdue loans, calculate fines, and check active loans for users.
 */
public class LibrarianService {

    private FileLoanRepository loanRepository = new FileLoanRepository();

    /**
     * Default constructor for the LibrarianService class.
     */
    public LibrarianService() {}

    /**
     * Returns a list of all overdue loans based on the current date.
     *
     * @return a list of overdue loans
     */
    public List<Loan> getOverdueLoans() {
        return getOverdueLoans(LocalDate.now());
    }

    /**
     * Returns a list of overdue loans based on the provided current date.
     *
     * @param currentDate the date used to check if loans are overdue
     * @return a list of overdue loans
     */
    public List<Loan> getOverdueLoans(LocalDate currentDate) {
        return loanRepository.getOverdueLoans(currentDate);
    }

    /**
     * Returns a list of all active loans, regardless of their overdue status.
     *
     * @return a list of all active loans
     */
    public List<Loan> getAllActiveLoans() {
        return loanRepository.getAllActiveLoans();
    }

    /**
     * Returns a list of overdue loans for a specific user based on the current date.
     *
     * @param username the username of the user whose overdue loans are being checked
     * @return a list of overdue loans for the specified user
     */
    public List<Loan> getOverdueLoansForUser(String username) {
        return getOverdueLoansForUser(username, LocalDate.now());
    }

    /**
     * Returns a list of overdue loans for a specific user based on the provided current date.
     *
     * @param username the username of the user whose overdue loans are being checked
     * @param currentDate the date used to check if loans are overdue
     * @return a list of overdue loans for the specified user
     */
    public List<Loan> getOverdueLoansForUser(String username, LocalDate currentDate) {
        return loanRepository.getActiveLoansForUser(username).stream()
                .filter(loan -> loan.isOverdue(currentDate))
                .toList();
    }

    /**
     * Calculates the total fine due for all overdue loans based on the current date.
     *
     * @param currentDate the date used to calculate the fine
     * @return the total fine due for all overdue loans
     */
    public int calculateTotalFineDue(LocalDate currentDate) {
        return getOverdueLoans(currentDate).stream()
                .mapToInt(loan -> loan.calculateFine(currentDate))
                .sum();
    }
}
