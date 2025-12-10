package Presentation;
import Domain.User;
import Service.FileUserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class SignUp {

    private FileUserRepository repo = new FileUserRepository();
    private Scanner scanner = new Scanner(System.in);

    public void show() {
        System.out.println("======== Sign Up ========");

        String username;
        while (true) {
            System.out.print("Enter your username: ");
            username = scanner.nextLine();

            if (repo.isUsernameExists(username) == false) {
                break;
            } else {
                System.out.println("Username already exists! Please choose another one.");
            }
        }


        System.out.print("Enter your email: ");
        String email = scanner.nextLine();


        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(new Date());

        Date currentDate = null;
        try {
            currentDate = dateFormat.parse(formattedDate);
            System.out.println(dateFormat.format(currentDate));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        boolean success = repo.addUser(username, password, email,currentDate);

        if (success) {
            System.out.println("Sign Up successful!");
            System.out.println("Your username is: " + username);
            System.out.println("Role: customer");
            User foundUser = new User(username, password, "customer", email,currentDate);
            CustomerMenuUI customerMenu = new CustomerMenuUI();
            customerMenu.show(foundUser);
        } else {
            System.out.println("Sign Up failed. Try again.");
        }
    }
}
