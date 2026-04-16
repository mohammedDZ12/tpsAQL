package org.example.tp3.ex2;
public class OrderService {
    private final OrderDao orderDao;
    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }
    public void createOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order must not be null");
        }
        orderDao.saveOrder(order);
    }
}
