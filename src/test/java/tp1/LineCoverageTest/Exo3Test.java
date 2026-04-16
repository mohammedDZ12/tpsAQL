package tp1.LineCoverageTest;
import org.junit.jupiter.api.Test;
import org.example.tp1.BinarySearch;
import static org.junit.jupiter.api.Assertions.*;
public class Exo3Test {
    int[] array = {1, 3, 5, 7, 9};
    @Test
    void elementFound() {
        assertEquals(2, BinarySearch.binarySearch(array, 5));
    }
    @Test
    void elementNotFound() {
        assertEquals(-1, BinarySearch.binarySearch(array, 4));
    }
    @Test
    void nullArray() {
        assertThrows(NullPointerException.class,
                () -> BinarySearch.binarySearch(null, 5));
    }
    @Test
    void elementAtStart() {
        assertEquals(0, BinarySearch.binarySearch(array, 1));
    }
}