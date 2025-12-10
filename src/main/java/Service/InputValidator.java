package Service;

import java.util.Scanner;

/**
 * The InputValidator class provides utility methods for validating user input.
 * It ensures that the user enters valid input (in this case, a valid integer)
 * and prompts the user again if the input is invalid.
 */
public class InputValidator {

    private static Scanner cin = new Scanner(System.in);

    /**
     * Prompts the user to enter an integer and keeps asking until a valid integer is entered.
     * This method uses a try-catch block to handle invalid input, such as when the user enters non-numeric characters.
     *
     * @return a valid integer entered by the user
     */
    public static int getValidIntegerInput() {
        while (true) {
            try {
                // Read user input and attempt to parse it as an integer
                return Integer.parseInt(cin.nextLine().trim());
            } catch (NumberFormatException e) {
                // If the input is not a valid integer, print an error message and prompt again
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }
}
