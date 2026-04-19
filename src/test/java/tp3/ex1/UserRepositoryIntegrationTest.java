package tp3.ex1;

import org.example.tp3.ex1.JdbcUserRepository;
import org.example.tp3.ex1.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Testcontainers
public class UserRepositoryIntegrationTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("testdb")
            .withUsername("root")
            .withPassword("password");

    private static Connection connection;
    private JdbcUserRepository userRepository;

    @BeforeAll
    static void beforeAll() throws Exception {
        connection = DriverManager.getConnection(
                mySQLContainer.getJdbcUrl(),
                mySQLContainer.getUsername(),
                mySQLContainer.getPassword()
        );
        
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE users (" +
                    "id BIGINT PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "email VARCHAR(255)" +
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
            statement.execute("TRUNCATE TABLE users");
            statement.execute("INSERT INTO users (id, name, email) VALUES (1, 'Alice', 'alice@example.com')");
            statement.execute("INSERT INTO users (id, name, email) VALUES (42, 'Bob', 'bob@example.com')");
        }
        userRepository = new JdbcUserRepository(connection);
    }

    @Test
    void testFindUserById_ReturnsUser() {
        User user = userRepository.findUserById(42L);
        assertNotNull(user);
        assertEquals(42L, user.getId());
        assertEquals("Bob", user.getName());
        assertEquals("bob@example.com", user.getEmail());
    }

    @Test
    void testFindUserById_NotFound_ReturnsNull() {
        User user = userRepository.findUserById(999L);
        assertNull(user);
    }
}
