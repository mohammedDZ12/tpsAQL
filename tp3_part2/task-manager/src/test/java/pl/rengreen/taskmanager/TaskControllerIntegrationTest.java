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
import pl.rengreen.taskmanager.service.TaskService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests d'intégration pour TaskController – utilisation de Testcontainers + MySQL Docker.
 *
 * Approche : au lieu de mocker le dépôt ou d'utiliser H2 in-memory,
 * on démarre un vrai conteneur MySQL via Testcontainers pour garantir
 * un environnement de test isolé et fidèle à la production.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = TaskControllerIntegrationTest.DockerMysqlDataSourceInitializer.class)
public class TaskControllerIntegrationTest {

    /**
     * Conteneur MySQL partagé par tous les tests de la classe.
     * Il est démarré une seule fois (ClassRule) et arrêté automatiquement
     * à la fin de la suite de tests.
     */
    @ClassRule
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    /**
     * Initializer Spring qui injecte dynamiquement l'URL JDBC générée par Testcontainers
     * dans le contexte applicatif avant le démarrage.
     */
    public static class DockerMysqlDataSourceInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
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
    private TaskService taskService;

    // -------------------------------------------------------------------------
    // Scénario 1 : Création d'une tâche
    // -------------------------------------------------------------------------

    /**
     * Vérifie qu'une tâche créée est bien persistée dans la base MySQL du conteneur
     * et qu'elle peut être récupérée par son identifiant.
     */
    @Test
    public void testCreateTask() {
        // Création d'une nouvelle tâche
        Task task = new Task("Tâche Test Création", "Description de la tâche de création",
                LocalDate.now().plusDays(7), false, "Admin");

        // Persistance
        taskService.createTask(task);

        // Vérification
        Long taskId = task.getId();
        assertNotNull("L'identifiant doit être généré après la persistance", taskId);

        Task retrieved = taskService.getTaskById(taskId);
        assertNotNull("La tâche doit être retrouvable par son identifiant", retrieved);
        assertEquals("Le nom de la tâche doit correspondre", task.getName(), retrieved.getName());
        assertEquals("La description doit correspondre", task.getDescription(), retrieved.getDescription());
        assertFalse("La tâche ne doit pas être complétée", retrieved.isCompleted());
    }

    // -------------------------------------------------------------------------
    // Scénario 2 : Récupération d'une tâche existante
    // -------------------------------------------------------------------------

    /**
     * Vérifie que la récupération d'une tâche par ID renvoie bien l'entité attendue.
     */
    @Test
    public void testGetTaskById() {
        Task task = createAndSaveTask("Tâche Test Récupération");

        Long taskId = task.getId();
        Task retrieved = taskService.getTaskById(taskId);

        assertNotNull("La tâche doit exister en base", retrieved);
        assertEquals("Les identifiants doivent correspondre", taskId, retrieved.getId());
        assertEquals("Le nom doit correspondre", task.getName(), retrieved.getName());
    }

    // -------------------------------------------------------------------------
    // Scénario 3 : Suppression d'une tâche
    // -------------------------------------------------------------------------

    /**
     * Vérifie qu'une tâche supprimée n'est plus accessible en base de données.
     */
    @Test
    public void testDeleteTask() {
        Task task = createAndSaveTask("Tâche Test Suppression");
        Long taskId = task.getId();

        // Suppression
        taskService.deleteTask(taskId);

        // Vérification : la tâche ne doit plus exister
        Task retrieved = taskService.getTaskById(taskId);
        assertNull("La tâche supprimée ne doit plus être trouvable", retrieved);
    }

    // -------------------------------------------------------------------------
    // Scénario 4 : Récupération de toutes les tâches
    // -------------------------------------------------------------------------

    /**
     * Vérifie que findAll retourne au moins les tâches insérées lors de ce test.
     * (Nouveau scénario ajouté – voir README)
     */
    @Test
    public void testFindAllTasks() {
        Task t1 = createAndSaveTask("Tâche ListeA");
        Task t2 = createAndSaveTask("Tâche ListeB");

        List<Task> all = taskService.findAll();

        assertNotNull("La liste ne doit pas être null", all);
        assertTrue("La liste doit contenir au moins les deux tâches insérées", all.size() >= 2);

        boolean containsT1 = all.stream().anyMatch(t -> t.getId().equals(t1.getId()));
        boolean containsT2 = all.stream().anyMatch(t -> t.getId().equals(t2.getId()));
        assertTrue("La tâche t1 doit être présente", containsT1);
        assertTrue("La tâche t2 doit être présente", containsT2);
    }

    // -------------------------------------------------------------------------
    // Scénario 5 : Mise à jour du statut complété
    // -------------------------------------------------------------------------

    /**
     * Vérifie que setTaskCompleted marque correctement la tâche comme complétée.
     * (Nouveau scénario ajouté – voir README)
     */
    @Test
    public void testSetTaskCompleted() {
        Task task = createAndSaveTask("Tâche Test Complétion");
        Long taskId = task.getId();

        taskService.setTaskCompleted(taskId);

        Task retrieved = taskService.getTaskById(taskId);
        assertNotNull(retrieved);
        assertTrue("La tâche doit être marquée comme complétée", retrieved.isCompleted());
    }

    // -------------------------------------------------------------------------
    // Scénario 6 : Mise à jour d'une tâche
    // -------------------------------------------------------------------------

    /**
     * Vérifie que updateTask modifie bien les champs de la tâche en base.
     * (Nouveau scénario ajouté – voir README)
     */
    @Test
    public void testUpdateTask() {
        Task original = createAndSaveTask("Tâche Originale");
        Long taskId = original.getId();

        Task updated = new Task("Tâche Mise à Jour", "Nouvelle description",
                LocalDate.now().plusDays(14), false, "Admin");

        taskService.updateTask(taskId, updated);

        Task retrieved = taskService.getTaskById(taskId);
        assertNotNull(retrieved);
        assertEquals("Le nom mis à jour doit correspondre", "Tâche Mise à Jour", retrieved.getName());
        assertEquals("La description mise à jour doit correspondre", "Nouvelle description", retrieved.getDescription());
    }

    // -------------------------------------------------------------------------
    // Scénario 7 : Tâches libres (sans propriétaire et non complétées)
    // -------------------------------------------------------------------------

    /**
     * Vérifie que findFreeTasks retourne uniquement les tâches sans propriétaire
     * et non complétées.
     * (Nouveau scénario ajouté – voir README)
     */
    @Test
    public void testFindFreeTasks() {
        Task freeTask = createAndSaveTask("Tâche Libre");

        List<Task> freeTasks = taskService.findFreeTasks();

        assertNotNull(freeTasks);
        boolean containsFree = freeTasks.stream().anyMatch(t -> t.getId().equals(freeTask.getId()));
        assertTrue("La tâche libre doit figurer dans la liste des tâches libres", containsFree);
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Task createAndSaveTask(String name) {
        Task task = new Task(name, "Description de " + name,
                LocalDate.now().plusDays(3), false, "Admin");
        taskService.createTask(task);
        return task;
    }
}
