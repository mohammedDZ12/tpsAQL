package pl.rengreen.taskmanager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test de chargement du contexte Spring Boot avec la base H2 embarquée.
 * Ce test vérifie uniquement que le contexte applicatif démarre sans erreur.
 * Il utilise H2 au lieu de MySQL pour ne pas dépendre de Docker.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class TaskManagerApplicationTests {

    @Test
    public void contextLoads() {
        // Vérifie que le contexte Spring Boot se charge correctement
    }

}
