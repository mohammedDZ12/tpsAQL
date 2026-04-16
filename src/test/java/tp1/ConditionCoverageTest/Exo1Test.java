package tp1.ConditionCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.Palindrome;
import static org.junit.jupiter.api.Assertions.*;
public class Exo1Test {
    @Test
    void c1_NullIsTrue() {
        assertThrows(NullPointerException.class,
                () -> Palindrome.isPalindrome(null));
    }
    @Test
    void c1_NullIsFalse_c2_True_c3_False() {
        assertTrue(Palindrome.isPalindrome("kayak"));
    }
    @Test
    void c2_WhileIsFalse() {
        assertTrue(Palindrome.isPalindrome(""));
    }
    @Test
    void c3_MismatchIsTrue() {
        assertFalse(Palindrome.isPalindrome("hello"));
    }
}
