package Service;

import Domain.Loan;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for librarians to manage and query loans.
 */
public class LibrarianService {

    private FileLoanRepository loanRepository = new FileLoanRepository();

    public LibrarianService() {}

    /**
     * Returns a list of all overdue loans as of today.
     *
     * @return list of overdue loans
     */
    public List<Loan> getOverdueLoans() {
        return getOverdueLoans(LocalDate.now());
    }

    /**
     * Returns a list of all overdue loans as of the specified date.
     *
     * @param currentDate the date to check for overdue loans
     * @return list of overdue loans
     */
    public List<Loan> getOverdueLoans(LocalDate currentDate) {
        return loanRepository.getOverdueLoans(currentDate);
    }

    /**
     * Returns a list of all active loans.
     *
     * @return list of active loans
     */
    public List<Loan> getAllActiveLoans() {
        return loanRepository.getAllActiveLoans();
    }

    /**
     * Returns a list of overdue loans for a specific user as of today.
     *
     * @param username the username of the user
     * @return list of overdue loans for the user
     */
    public List<Loan> getOverdueLoansForUser(String username) {
        return getOverdueLoansForUser(username, LocalDate.now());
    }

    /**
     * Returns a list of overdue loans for a specific user as of the specified date.
     *
     * @param username the username of the user
     * @param currentDate the date to check for overdue loans
     * @return list of overdue loans for the user
     */
    public List<Loan> getOverdueLoansForUser(String username, LocalDate currentDate) {
        return loanRepository.getActiveLoansForUser(username).stream()
                .filter(loan -> loan.isOverdue(currentDate))
                .toList();
    }

    /**
     * Calculates the total fines for all overdue loans as of the specified date.
     *
     * @param currentDate the date to calculate fines for
     * @return total fine amount
     */
    public int calculateTotalFineDue(LocalDate currentDate) {
        return getOverdueLoans(currentDate).stream()
                .mapToInt(loan -> loan.calculateFine(currentDate))
                .sum();
    }
}
