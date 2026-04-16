package tp1.ConditionCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.FizzBuzz;
import static org.junit.jupiter.api.Assertions.*;
public class Exo6Test {
    @Test
    void c1_NotPositiveTrue() {
        assertThrows(IllegalArgumentException.class,
                () -> FizzBuzz.fizzBuzz(0));
    }
    @Test
    void c2_Mod15True() {
        assertEquals("FizzBuzz", FizzBuzz.fizzBuzz(15));
    }
    @Test
    void c3_Mod3True() {
        assertEquals("Fizz", FizzBuzz.fizzBuzz(3));
    }
    @Test
    void c4_Mod5True() {
        assertEquals("Buzz", FizzBuzz.fizzBuzz(5));
    }
    @Test
    void c4_Mod5False() {
        assertEquals("7", FizzBuzz.fizzBuzz(7));
    }
}
