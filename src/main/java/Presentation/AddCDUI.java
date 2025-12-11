package Presentation;

import Domain.CD;
import Service.BookServiceAdmin;

import java.util.Scanner;

/**
 * User interface class responsible for adding a new CD to the system.
 * <p>
 * This class interacts with the administrator service layer to create
 * a new {@link CD} object and store it along with the desired number of copies.
 * It collects input from the console, validates it, and forwards the
 * necessary data to the domain logic.
 * </p>
 */
public class AddCDUI {
    /** Scanner used to read user input from the console. */
    private Scanner cin = new Scanner(System.in);

    /**
     * Displays the CD creation form, reads user input, validates the number
     * of copies, and calls the service layer to add the CD.
     *
     * @param service the admin service used to add CDs to the system
     */
    public void show(BookServiceAdmin service) {
        System.out.println("\n====== Add CD ======");

        System.out.print("CD Title: ");
        String title = cin.nextLine();

        System.out.print("Artist/Author: ");
        String author = cin.nextLine();

        System.out.print("ISBN/ID: ");
        String isbn = cin.nextLine();

        System.out.print("Number of copies: ");
        int numberOfCopies = 1;
        try {
            numberOfCopies = Integer.parseInt(cin.nextLine());
            if (numberOfCopies <= 0) {
                System.out.println("Number of copies must be at least 1. Using 1 copy.");
                numberOfCopies = 1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using 1 copy.");
        }

        CD cd = new CD(title, author, isbn);
        String result = service.addCD(cd, numberOfCopies)
                ? "Book added with " + numberOfCopies + " copies!"
                : "A book or CD with this ISBN already exists!";
        System.out.println(result);

        System.out.println("========================");
    }
}
