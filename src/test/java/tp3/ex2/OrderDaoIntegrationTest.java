package tp3.ex2;

import org.example.tp3.ex2.JdbcOrderDao;
import org.example.tp3.ex2.Order;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class OrderDaoIntegrationTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("testdb")
            .withUsername("root")
            .withPassword("password");

    private static Connection connection;
    private JdbcOrderDao orderDao;

    @BeforeAll
    static void beforeAll() throws Exception {
        connection = DriverManager.getConnection(
                mySQLContainer.getJdbcUrl(),
                mySQLContainer.getUsername(),
                mySQLContainer.getPassword()
        );

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE orders (" +
                    "id VARCHAR(255) PRIMARY KEY, " +
                    "product VARCHAR(255), " +
                    "quantity INT, " +
                    "price DOUBLE" +
                    ")");
        }
    }

    @AfterAll
    static void afterAll() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE orders");
        }
        orderDao = new JdbcOrderDao(connection);
    }

    @Test
    void testSaveOrder() throws Exception {
        Order order = new Order("CMD-001", "Laptop", 1, 999.99);
        orderDao.saveOrder(order);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM orders WHERE id='CMD-001'")) {
            assertTrue(resultSet.next(), "L'ordre devrait être sauvegardé en base de données");
            assertEquals("CMD-001", resultSet.getString("id"));
            assertEquals("Laptop", resultSet.getString("product"));
            assertEquals(1, resultSet.getInt("quantity"));
            assertEquals(999.99, resultSet.getDouble("price"));
        }
    }
}
