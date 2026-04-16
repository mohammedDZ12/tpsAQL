package tp1.ConditionCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.BinarySearch;
import static org.junit.jupiter.api.Assertions.*;
public class Exo3Test {
    int[] array = {1, 3, 5, 7, 9};
    @Test
    void c1_NullArrayIsTrue() {
        assertThrows(NullPointerException.class,
                () -> BinarySearch.binarySearch(null, 5));
    }
    @Test
    void c2_WhileConditionIsFalse() {
        assertEquals(-1, BinarySearch.binarySearch(new int[]{}, 5));
    }
    @Test
    void c3_ElementFoundIsTrue() {
        assertEquals(2, BinarySearch.binarySearch(array, 5));
    }
    @Test
    void c4_SearchRightIsTrue() {
        assertEquals(4, BinarySearch.binarySearch(array, 9));
    }
    @Test
    void c4_SearchLeftIsFalse() {
        assertEquals(0, BinarySearch.binarySearch(array, 1));
    }
}
