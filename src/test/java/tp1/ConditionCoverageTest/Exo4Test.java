package tp1.ConditionCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.QuadraticEquation;
import static org.junit.jupiter.api.Assertions.*;
public class Exo4Test {
    @Test
    void c1_AIsZeroTrue() {
        assertThrows(IllegalArgumentException.class,
                () -> QuadraticEquation.solve(0, 1, 1));
    }
    @Test
    void c2_DeltaNegativeTrue() {
        assertNull(QuadraticEquation.solve(1, 1, 1));
    }
    @Test
    void c3_DeltaZeroTrue() {
        double[] result = QuadraticEquation.solve(1, 2, 1);
        assertEquals(1, result.length);
        assertEquals(-1.0, result[0]);
    }
    @Test
    void c3_DeltaPositiveFalse() {
        double[] result = QuadraticEquation.solve(1, -3, 2);
        assertEquals(2, result.length);
        assertEquals(2.0, result[0]);
        assertEquals(1.0, result[1]);
    }
}
