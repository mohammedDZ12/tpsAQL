package tp1.LineCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.Anagram;
import static org.junit.jupiter.api.Assertions.*;
public class Exo2Test {
    @Test
    void anagramTrue() {
        assertTrue(Anagram.isAnagram("chien", "niche"));
    }
    @Test
    void differentLength() {
        assertFalse(Anagram.isAnagram("chien", "chat"));
    }
    @Test
    void sameLengthNotAnagram() {
        assertFalse(Anagram.isAnagram("abc", "abd"));
    }
    @Test
    void nullTest() {
        assertThrows(NullPointerException.class,
                () -> Anagram.isAnagram(null, "abc"));
    }
}
