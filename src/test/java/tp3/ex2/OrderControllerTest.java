package tp3.ex2;
import org.example.tp3.ex2.Order;
import org.example.tp3.ex2.OrderController;
import org.example.tp3.ex2.OrderDao;
import org.example.tp3.ex2.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    @Mock
    private OrderDao orderDao;
    private OrderService orderService;
    private OrderController orderController;
    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderDao);
        orderController = new OrderController(orderService);
    }
    @Test
    @DisplayName("createOrder doit appeler OrderService.createOrder avec la bonne commande")
    void testCreateOrder_CallsServiceWithCorrectOrder() {
        Order order = new Order("CMD-001", "Laptop", 1, 999.99);
        OrderService mockOrderService = mock(OrderService.class);
        OrderController controller = new OrderController(mockOrderService);
        controller.createOrder(order);
        verify(mockOrderService, times(1)).createOrder(order);
    }
    @Test
    @DisplayName("createOrder doit propager l'appel jusqu'à OrderDao.saveOrder")
    void testCreateOrder_PropagatesCallToDao() {
        Order order = new Order("CMD-002", "Smartphone", 2, 499.99);
        orderController.createOrder(order);
        verify(orderDao, times(1)).saveOrder(order);
    }
    @Test
    @DisplayName("createOrder avec commande null doit lever IllegalArgumentException")
    void testCreateOrder_NullOrder_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> orderController.createOrder(null));
        verify(orderDao, never()).saveOrder(any());
    }
    @Test
    @DisplayName("createOrder doit passer l'objet commande exact au DAO")
    void testCreateOrder_PassesExactOrderObjectToDao() {
        Order order = new Order("CMD-003", "Tablet", 3, 299.99);
        orderController.createOrder(order);
        verify(orderDao).saveOrder(argThat(savedOrder ->
                savedOrder.getId().equals("CMD-003") &&
                savedOrder.getProduct().equals("Tablet") &&
                savedOrder.getQuantity() == 3 &&
                savedOrder.getPrice() == 299.99
        ));
    }
    @Test
    @DisplayName("createOrder ne doit appeler saveOrder qu'une seule fois")
    void testCreateOrder_SaveOrderCalledExactlyOnce() {
        Order order = new Order("CMD-004", "Monitor", 1, 350.00);
        orderController.createOrder(order);
        verify(orderDao, times(1)).saveOrder(any());
    }
}
