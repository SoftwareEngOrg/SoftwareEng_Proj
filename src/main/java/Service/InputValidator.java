package Service;
import java.util.Scanner;
public class InputValidator {
    private static Scanner cin = new Scanner(System.in);

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
