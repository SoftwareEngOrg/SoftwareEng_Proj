package Presentation;

import Service.AdminService;
import java.util.Scanner;


public class LoginUI {

    private AdminService adminService = new AdminService();
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

            if(adminService.login(user, pass))
            {
                System.out.println("Login successful!");
                new AdminMenuUI().show(adminService);
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
