package tp1.BranchCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.Anagram;
import static org.junit.jupiter.api.Assertions.*;
public class Exo2Test {
    @Test
    void trueAnagram() {
        assertTrue(Anagram.isAnagram("listen", "silent"));
    }
    @Test
    void falseAnagram() {
        assertFalse(Anagram.isAnagram("abc", "abd"));
    }
    @Test
    void differentLength() {
        assertFalse(Anagram.isAnagram("abc", "ab"));
    }
    @Test
    void nullFirstString() {
        assertThrows(NullPointerException.class,
                () -> Anagram.isAnagram(null, "abc"));
    }
    @Test
    void nullSecondString() {
        assertThrows(NullPointerException.class,
                () -> Anagram.isAnagram("abc", null));
    }
}