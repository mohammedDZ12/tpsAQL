package tp1.LineCoverageTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.example.tp1.Palindrome;
public class Exo1Test {
    @Test
    void testPalindromeTrue() {
        assertTrue(Palindrome.isPalindrome("kayak"));
    }
    @Test
    void testPalindromeFalse() {
        assertFalse(Palindrome.isPalindrome("hello"));
    }
    @Test
    void testNull() {
        assertThrows(NullPointerException.class,
                () -> Palindrome.isPalindrome(null));
    }
    @Test
    void testSingleChar() {
        assertTrue(Palindrome.isPalindrome("a"));
    }
}