package org.example.tp3.ex2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcOrderDao implements OrderDao {
    private final Connection connection;

    public JdbcOrderDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void saveOrder(Order order) {
        String query = "INSERT INTO orders (id, product, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, order.getId());
            statement.setString(2, order.getProduct());
            statement.setInt(3, order.getQuantity());
            statement.setDouble(4, order.getPrice());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order", e);
        }
    }
}
