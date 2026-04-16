package org.example.tp3.ex2;
public class Order {
    private String id;
    private String product;
    private int quantity;
    private double price;
    public Order(String id, String product, int quantity, double price) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }
    public String getId() { return id; }
    public String getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    @Override
    public String toString() {
        return "Order{id='" + id + "', product='" + product + "', quantity=" + quantity + ", price=" + price + "}";
    }
}
