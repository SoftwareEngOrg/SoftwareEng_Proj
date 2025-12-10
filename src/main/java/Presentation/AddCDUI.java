package Presentation;

import Domain.CD;
import Service.BookServiceAdmin;

import java.util.Scanner;

public class AddCDUI {
    private Scanner cin = new Scanner(System.in);

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
