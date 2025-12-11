package Service;

import java.util.Scanner;

/**
 * Utility class for validating user input from the console.
 */
public class InputValidator {

    private static Scanner cin = new Scanner(System.in);

    /**
     * Prompts the user and returns a valid integer input.
     * Keeps asking until a valid integer is entered.
     *
     * @return the validated integer input
     */
    public static int getValidIntegerInput() {
        while (true) {
            try {
                return Integer.parseInt(cin.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }
}
