package tp1.BranchCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.Palindrome;
import static org.junit.jupiter.api.Assertions.*;
public class Exo1Test {
    @Test
    void palindromeNull() {
        assertThrows(NullPointerException.class,
                () -> Palindrome.isPalindrome(null));
    }
    @Test
    void palindromeTrue() {
        assertTrue(Palindrome.isPalindrome("radar"));
    }
    @Test
    void palindromeFalse() {
        assertFalse(Palindrome.isPalindrome("java"));
    }
    @Test
    void palindromeEmpty() {
        assertTrue(Palindrome.isPalindrome(""));
    }
}