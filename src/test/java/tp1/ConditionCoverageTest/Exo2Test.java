package tp1.ConditionCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.Anagram;
import static org.junit.jupiter.api.Assertions.*;
public class Exo2Test {
    @Test
    void c1_S1IsNull() {
        assertThrows(NullPointerException.class,
                () -> Anagram.isAnagram(null, "abc"));
    }
    @Test
    void c2_S2IsNull() {
        assertThrows(NullPointerException.class,
                () -> Anagram.isAnagram("abc", null));
    }
    @Test
    void c3_LengthDifferent() {
        assertFalse(Anagram.isAnagram("abc", "ab"));
    }
    @Test
    void c3_SameLength_c4_CountNotZero() {
        assertFalse(Anagram.isAnagram("abc", "abd"));
    }
    @Test
    void c4_CountIsZero() {
        assertTrue(Anagram.isAnagram("listen", "silent"));
    }
}
