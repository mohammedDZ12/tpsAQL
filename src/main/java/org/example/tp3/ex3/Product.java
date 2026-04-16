package org.example.tp3.ex3;
public class Product {
    private String id;
    private String name;
    private double price;
    private String category;
    public Product(String id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    @Override
    public String toString() {
        return "Product{id='" + id + "', name='" + name + "', price=" + price + ", category='" + category + "'}";
    }
}
