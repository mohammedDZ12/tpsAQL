package tp1.BranchCoverageTest;
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
    void negDelta() {
        assertNull(QuadraticEquation.solve(1, 1, 1));
    }
    @Test
    void zeroDelta() {
        double[] result = QuadraticEquation.solve(1, 2, 1);
        assertEquals(1, result.length);
        assertEquals(-1.0, result[0]);
    }
    @Test
    void posDelta() {
        double[] result = QuadraticEquation.solve(1, -3, 2);
        assertEquals(2, result.length);
        assertEquals(2.0, result[0]);
        assertEquals(1.0, result[1]);
    }
}
