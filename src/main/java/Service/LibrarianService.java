package Service;

import Domain.Loan;

import java.time.LocalDate;
import java.util.List;

public class LibrarianService {
    private  FileLoanRepository loanRepository = new FileLoanRepository();


    public LibrarianService(){};
    // For testing: allow injecting a fake date
    public List<Loan> getOverdueLoans() {
        return getOverdueLoans(LocalDate.now());
    }

    public List<Loan> getOverdueLoans(LocalDate currentDate) {
        return loanRepository.getOverdueLoans(currentDate);
    }

    public List<Loan> getAllActiveLoans() {
        return loanRepository.getAllActiveLoans();
    }

    public List<Loan> getOverdueLoansForUser(String username) {
        return getOverdueLoansForUser(username, LocalDate.now());
    }

    public List<Loan> getOverdueLoansForUser(String username, LocalDate currentDate) {
        return loanRepository.getActiveLoansForUser(username).stream()
                .filter(loan -> loan.isOverdue(currentDate))
                .toList();
    }

    public int calculateTotalFineDue(LocalDate currentDate) {
        return getOverdueLoans(currentDate).stream()
                .mapToInt(loan -> loan.calculateFine(currentDate))
                .sum();
    }
}
