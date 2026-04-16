package tp1.BranchCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.BinarySearch;
import static org.junit.jupiter.api.Assertions.*;
public class Exo3Test {
    int[] array = {1, 3, 5, 7, 9};
    @Test
    void nullArray() {
        assertThrows(NullPointerException.class,
                () -> BinarySearch.binarySearch(null, 5));
    }
    @Test
    void emptyArray() {
        assertEquals(-1, BinarySearch.binarySearch(new int[]{}, 5));
    }
    @Test
    void elementFound() {
        assertEquals(2, BinarySearch.binarySearch(array, 5));
    }
    @Test
    void elementNotFoundSearchRight() {
        assertEquals(-1, BinarySearch.binarySearch(array, 6));
    }
    @Test
    void elementNotFoundSearchLeft() {
        assertEquals(-1, BinarySearch.binarySearch(array, 2));
    }
}
