package tp1.LineCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.RomanNumeral;
import static org.junit.jupiter.api.Assertions.*;
public class Exo5Test {
    @Test
    void tooSmall() {
        assertThrows(IllegalArgumentException.class,
                () -> RomanNumeral.toRoman(0));
    }
    @Test
    void tooLarge() {
        assertThrows(IllegalArgumentException.class,
                () -> RomanNumeral.toRoman(4000));
    }
    @Test
    void roman1() {
        assertEquals("I", RomanNumeral.toRoman(1));
    }
    @Test
    void roman3999() {
        assertEquals("MMMCMXCIX", RomanNumeral.toRoman(3999));
    }
    @Test
    void roman1994() {
        assertEquals("MCMXCIV", RomanNumeral.toRoman(1994));
    }
}
