package tp1.BranchCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.FizzBuzz;
import static org.junit.jupiter.api.Assertions.*;
public class Exo6Test {
    @Test
    void notPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> FizzBuzz.fizzBuzz(0));
    }
    @Test
    void fizzBuzz15() {
        assertEquals("FizzBuzz", FizzBuzz.fizzBuzz(15));
    }
    @Test
    void fizz3() {
        assertEquals("Fizz", FizzBuzz.fizzBuzz(3));
    }
    @Test
    void buzz5() {
        assertEquals("Buzz", FizzBuzz.fizzBuzz(5));
    }
    @Test
    void plain7() {
        assertEquals("7", FizzBuzz.fizzBuzz(7));
    }
}
