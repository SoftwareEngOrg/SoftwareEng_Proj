package Presentation;

import Domain.User;
import Service.AdminService;
import Service.FileUserRepository;

import java.util.Scanner;


public class LoginUI {

    private FileUserRepository userRepo = new FileUserRepository();
    private Scanner cin = new Scanner(System.in);

    public void show()
    {
        boolean nonvalidUser = true;

        while (nonvalidUser)
        {
            System.out.println();
            System.out.println("======== Hello and welcome to our Library ========");
            System.out.print("Username: ");
            String user = cin.nextLine();
            System.out.print("Password: ");
            String pass = cin.nextLine();
            User foundUser = userRepo.findUser(user, pass );


            if(foundUser != null)
            {
                System.out.println(foundUser.getEmail());
                System.out.println("Login successful!");
                if(foundUser.getRole().equals("admin"))
                {
                    AdminService adminService = new AdminService();
                    adminService.login(user, pass);
                    new AdminMenuUI().show(adminService);
                }
                else if(foundUser.getRole().equals("customer"))
                {
                    userRepo.updateDate(foundUser);
                    CustomerMenuUI customerMenu = new CustomerMenuUI();
                    customerMenu.show(foundUser);  // Pass the actual User object!
                }
                else if(foundUser.getRole().equals("librarian"))
                {
                    System.out.println("Welcome, Librarian " + foundUser.getUsername() + "!");
                    new LibrarianMenuUI().show();  // This is the new menu
                }
                nonvalidUser = false;
            }
            else
            {
                System.out.println("Invalid credentials");
                System.out.println("======================");
                System.out.println();
            }

        }

    }

}
