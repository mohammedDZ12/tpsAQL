package tp1.ConditionCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.RomanNumeral;
import static org.junit.jupiter.api.Assertions.*;
public class Exo5Test {
    @Test
    void c1_NLessThanOneTrue() {
        assertThrows(IllegalArgumentException.class,
                () -> RomanNumeral.toRoman(0));
    }
    @Test
    void c2_NGreaterThan3999True() {
        assertThrows(IllegalArgumentException.class,
                () -> RomanNumeral.toRoman(4000));
    }
    @Test
    void c1c2_BothFalse_c3_TrueAndFalse() {
        assertEquals("I", RomanNumeral.toRoman(1));
    }
    @Test
    void multipleSymbols() {
        assertEquals("MCMXCIV", RomanNumeral.toRoman(1994));
    }
    @Test
    void maximumValue() {
        assertEquals("MMMCMXCIX", RomanNumeral.toRoman(3999));
    }
}
