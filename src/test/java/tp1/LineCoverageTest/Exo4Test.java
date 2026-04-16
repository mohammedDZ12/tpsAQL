package tp1.LineCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.QuadraticEquation;
import static org.junit.jupiter.api.Assertions.*;
public class Exo4Test {
    @Test
    void aIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> QuadraticEquation.solve(0, 1, 1));
    }
    @Test
    void noSolution() {
        assertNull(QuadraticEquation.solve(1, 1, 1));
    }
    @Test
    void oneSolution() {
        double[] result = QuadraticEquation.solve(1, 2, 1);
        assertEquals(1, result.length);
    }
    @Test
    void twoSolutions() {
        double[] result = QuadraticEquation.solve(1, -3, 2);
        assertEquals(2, result.length);
    }
}