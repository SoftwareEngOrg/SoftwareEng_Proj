// src/test/java/Service/InputValidatorTest.java

package Service;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InputValidatorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        outContent.reset();
    }

    private void setInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        // Re-create scanner with new input
        try {
            var field = InputValidator.class.getDeclaredField("cin");
            field.setAccessible(true);
            field.set(null, new java.util.Scanner(System.in));
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset Scanner", e);
        }
    }

    private String getOutput() {
        return outContent.toString().replace("\r\n", "\n").trim();
    }

    @Test
    @DisplayName("Accepts valid positive integer")
    void acceptsValidPositiveInteger() {
        setInput("42\n");
        assertEquals(42, InputValidator.getValidIntegerInput());
        assertTrue(getOutput().isEmpty()); // no error message
    }

    @Test
    @DisplayName("Accepts valid negative integer")
    void acceptsNegativeInteger() {
        setInput("-123\n");
        assertEquals(-123, InputValidator.getValidIntegerInput());
    }

    @Test
    @DisplayName("Accepts zero")
    void acceptsZero() {
        setInput("0\n");
        assertEquals(0, InputValidator.getValidIntegerInput());
    }

    @Test
    @DisplayName("Accepts Integer.MAX_VALUE and MIN_VALUE")
    void acceptsIntegerBounds() {
        setInput("2147483647\n");
        assertEquals(Integer.MAX_VALUE, InputValidator.getValidIntegerInput());

        setInput("-2147483648\n");
        assertEquals(Integer.MIN_VALUE, InputValidator.getValidIntegerInput());
    }

    @Test
    @DisplayName("Rejects non-numeric input and asks again")
    void rejectsLettersAndReprompts() {
        setInput("hello\nworld\n999\n");
        assertEquals(999, InputValidator.getValidIntegerInput());

        String output = getOutput();
        assertTrue(output.contains("Invalid input"));
        assertEquals(2, output.split("Invalid input").length - 1); // 2 errors
    }

    @Test
    @DisplayName("Rejects symbols and special characters")
    void rejectsSymbols() {
        setInput("!@#\n$$$\n123\n");
        assertEquals(123, InputValidator.getValidIntegerInput());
        assertEquals(2, getOutput().split("Invalid input").length - 1);
    }

    @Test
    @DisplayName("Rejects empty input")
    void rejectsEmptyLine() {
        setInput("\n\n\n50\n");
        assertEquals(50, InputValidator.getValidIntegerInput());
        assertEquals(3, getOutput().split("Invalid input").length - 1);
    }

    @Test
    @DisplayName("Rejects decimal numbers")
    void rejectsDecimalNumbers() {
        setInput("3.14\n12.0\n-0.5\n100\n");
        assertEquals(100, InputValidator.getValidIntegerInput());
        assertEquals(3, getOutput().split("Invalid input").length - 1);
    }

    @Test
    @DisplayName("Handles whitespace and trimming")
    void handlesWhitespace() {
        setInput("   777   \n");
        assertEquals(777, InputValidator.getValidIntegerInput());
    }

    @Test
    @DisplayName("Works with very large valid number just below overflow")
    void acceptsLargeValidNumber() {
        setInput("2147483646\n"); // MAX_VALUE - 1
        assertEquals(2147483646, InputValidator.getValidIntegerInput());
    }

    @Test
    @DisplayName("Keeps asking forever until valid input (no StackOverflow)")
    void loopsForeverUntilValid() {
        // Simulate 100 invalid inputs then a valid one
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            input.append("invalid\n");
        }
        input.append("555\n");

        setInput(input.toString());
        assertEquals(555, InputValidator.getValidIntegerInput());
        assertEquals(100, getOutput().split("Invalid input").length - 1);
    }

}