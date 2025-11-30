package Presentation;

import Service.InputValidator;

public class HelloApplication {
    public static void main(String[] args) {

        System.out.println("======== Hello and welcome to our Library ========");
        System.out.println("1. Login");
        System.out.println("2. signup");
        System.out.println("=========================");
        System.out.print("Choose: ");
        int choice = InputValidator.getValidIntegerInput();

        if(choice == 1)
        {
            LoginUI login = new LoginUI();
            login.show();
        }

        else if (choice == 2)
        {
            SignUp sign = new SignUp();
            sign.show();
        }


    }
}
