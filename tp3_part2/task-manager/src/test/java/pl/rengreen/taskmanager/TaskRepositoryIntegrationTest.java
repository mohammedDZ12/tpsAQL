package pl.rengreen.taskmanager;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.MySQLContainer;
import pl.rengreen.taskmanager.model.Task;
import pl.rengreen.taskmanager.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests d'intégration pour la couche Repository (TaskRepository).
 *
 * Ces tests vérifient que Spring Data JPA interagit correctement avec MySQL
 * en base réelle (conteneur Docker géré par Testcontainers), sans aucun mock.
 *
 * Complémentaires à TaskControllerIntegrationTest, ils ciblent la couche
 * d'accès aux données directement (sans passer par le service).
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = TaskRepositoryIntegrationTest.DataSourceInitializer.class)
public class TaskRepositoryIntegrationTest {

    /** Conteneur MySQL partagé sur la durée de la classe de test. */
    @ClassRule
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("repotestdb")
            .withUsername("repouser")
            .withPassword("repopass");

    public static class DataSourceInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext ctx) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(ctx,
                    "spring.datasource.url="      + mysql.getJdbcUrl(),
                    "spring.datasource.username=" + mysql.getUsername(),
                    "spring.datasource.password=" + mysql.getPassword(),
                    "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
                    "spring.jpa.hibernate.ddl-auto=create-drop",
                    "spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect"
            );
        }
    }

    @Autowired
    private TaskRepository taskRepository;

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------

    private Task persist(String name) {
        Task t = new Task(name, "Desc – " + name,
                LocalDate.now().plusDays(5), false, "TestUser");
        return taskRepository.save(t);
    }

    // ------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------

    /** Repository.save() doit générer un ID non nul. */
    @Test
    public void save_shouldGenerateId() {
        Task saved = persist("Repo-Save-Test");
        assertNotNull("L'ID doit être généré automatiquement", saved.getId());
    }

    /** findById() doit retrouver une entité persistée. */
    @Test
    public void findById_shouldReturnSavedTask() {
        Task saved = persist("Repo-FindById");
        Optional<Task> found = taskRepository.findById(saved.getId());
        assertTrue("La tâche doit exister", found.isPresent());
        assertEquals(saved.getName(), found.get().getName());
    }

    /** findById() doit retourner Optional.empty() pour un ID inexistant. */
    @Test
    public void findById_unknownId_shouldReturnEmpty() {
        Optional<Task> found = taskRepository.findById(Long.MAX_VALUE);
        assertFalse("Aucune tâche ne doit être trouvée pour un ID fictif", found.isPresent());
    }

    /** deleteById() doit supprimer l'entité de la base. */
    @Test
    public void deleteById_shouldRemoveTask() {
        Task saved = persist("Repo-Delete");
        Long id = saved.getId();

        taskRepository.deleteById(id);

        assertFalse("La tâche supprimée ne doit plus exister",
                taskRepository.findById(id).isPresent());
    }

    /** findAll() doit inclure toutes les tâches persistées. */
    @Test
    public void findAll_shouldContainAllSavedTasks() {
        Task t1 = persist("Repo-FindAll-A");
        Task t2 = persist("Repo-FindAll-B");

        List<Task> all = taskRepository.findAll();
        assertTrue("La tâche A doit être présente",
                all.stream().anyMatch(t -> t.getId().equals(t1.getId())));
        assertTrue("La tâche B doit être présente",
                all.stream().anyMatch(t -> t.getId().equals(t2.getId())));
    }

    /** La mise à jour d'un champ doit être persistée. */
    @Test
    public void save_afterModification_shouldPersistChanges() {
        Task saved = persist("Repo-Update-Before");
        saved.setName("Repo-Update-After");
        taskRepository.save(saved);

        Task reloaded = taskRepository.findById(saved.getId()).orElseThrow(
                () -> new AssertionError("La tâche doit exister après mise à jour"));
        assertEquals("Repo-Update-After", reloaded.getName());
    }

    /** count() doit refléter le nombre exact de tâches sauvegardées. */
    @Test
    public void count_shouldReflectNumberOfSavedTasks() {
        long before = taskRepository.count();
        persist("Repo-Count-A");
        persist("Repo-Count-B");
        long after = taskRepository.count();
        assertEquals("Le compteur doit augmenter de 2", before + 2, after);
    }
}
