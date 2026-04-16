package org.example.tp3.ex3;
public class ProductService {
    private final ProductApiClient productApiClient;
    public ProductService(ProductApiClient productApiClient) {
        this.productApiClient = productApiClient;
    }
    public Product getProduct(String productId) {
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("Product ID must not be null or empty");
        }
        return productApiClient.getProduct(productId);
    }
}
