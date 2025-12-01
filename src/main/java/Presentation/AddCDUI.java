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

        CD cd = new CD(title, author, isbn);
        boolean success = service.addCD(cd);
        System.out.println(success ? "CD added!" : "CD or Book with this ISBN already exists!");
    }
}
