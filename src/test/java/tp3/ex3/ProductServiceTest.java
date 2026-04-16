package tp3.ex3;
import org.example.tp3.ex3.ApiException;
import org.example.tp3.ex3.DataFormatException;
import org.example.tp3.ex3.Product;
import org.example.tp3.ex3.ProductApiClient;
import org.example.tp3.ex3.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductApiClient productApiClient;
    private ProductService productService;
    @BeforeEach
    void setUp() {
        productService = new ProductService(productApiClient);
    }
    @Test
    @DisplayName("getProduct doit appeler l'API avec le bon productId")
    void testGetProduct_CallsApiWithCorrectId() {
        String productId = "PROD-001";
        Product expected = new Product(productId, "Laptop Pro", 1299.99, "Electronics");
        when(productApiClient.getProduct(productId)).thenReturn(expected);
        productService.getProduct(productId);
        verify(productApiClient, times(1)).getProduct(productId);
    }
    @Test
    @DisplayName("getProduct doit retourner le produit renvoyé par l'API")
    void testGetProduct_ReturnsProductFromApi() {
        String productId = "PROD-002";
        Product expected = new Product(productId, "Wireless Mouse", 39.99, "Accessories");
        when(productApiClient.getProduct(productId)).thenReturn(expected);
        Product result = productService.getProduct(productId);
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Wireless Mouse", result.getName());
        assertEquals(39.99, result.getPrice());
        assertEquals("Accessories", result.getCategory());
    }
    @Test
    @DisplayName("getProduct doit propager DataFormatException si le format est incompatible")
    void testGetProduct_DataFormatException_IsPropagated() {
        String productId = "PROD-BAD-FORMAT";
        when(productApiClient.getProduct(productId))
                .thenThrow(new DataFormatException("Incompatible data format from API"));
        DataFormatException ex = assertThrows(DataFormatException.class,
                () -> productService.getProduct(productId));
        assertEquals("Incompatible data format from API", ex.getMessage());
        verify(productApiClient, times(1)).getProduct(productId);
    }
    @Test
    @DisplayName("getProduct doit propager ApiException en cas d'échec d'API")
    void testGetProduct_ApiException_IsPropagated() {
        String productId = "PROD-UNAVAILABLE";
        when(productApiClient.getProduct(productId))
                .thenThrow(new ApiException("API server unreachable"));
        ApiException ex = assertThrows(ApiException.class,
                () -> productService.getProduct(productId));
        assertEquals("API server unreachable", ex.getMessage());
        verify(productApiClient, times(1)).getProduct(productId);
    }
    @Test
    @DisplayName("getProduct doit propager ApiException avec timeout")
    void testGetProduct_ApiTimeout_ThrowsApiException() {
        String productId = "PROD-TIMEOUT";
        when(productApiClient.getProduct(productId))
                .thenThrow(new ApiException("Request timed out", new RuntimeException("timeout")));
        assertThrows(ApiException.class, () -> productService.getProduct(productId));
    }
    @Test
    @DisplayName("getProduct doit lever IllegalArgumentException si l'ID est null")
    void testGetProduct_NullId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.getProduct(null));
        verify(productApiClient, never()).getProduct(any());
    }
    @Test
    @DisplayName("getProduct doit lever IllegalArgumentException si l'ID est vide")
    void testGetProduct_EmptyId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.getProduct(""));
        verify(productApiClient, never()).getProduct(any());
    }
    @Test
    @DisplayName("getProduct ne doit appeler l'API qu'une seule fois")
    void testGetProduct_ApiCalledExactlyOnce() {
        String productId = "PROD-003";
        when(productApiClient.getProduct(productId))
                .thenReturn(new Product(productId, "Keyboard", 79.99, "Accessories"));
        productService.getProduct(productId);
        verify(productApiClient, times(1)).getProduct(productId);
        verify(productApiClient, never()).getProduct("OTHER-ID");
    }
}
