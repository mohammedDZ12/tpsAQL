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
import pl.rengreen.taskmanager.model.User;
import pl.rengreen.taskmanager.service.TaskService;
import pl.rengreen.taskmanager.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests d'intégration pour UserService avec Testcontainers.
 *
 * Scénarios couverts :
 *   - Création / récupération d'un utilisateur
 *   - Vérification d'unicité de l'e-mail
 *   - Suppression d'un utilisateur
 *   - Attribution / désattribution d'une tâche à un utilisateur
 *
 * Chaque conteneur MySQL est partagé sur toute la classe (@ClassRule) afin
 * d'éviter un démarrage répété, tout en garantissant l'isolation de schema
 * (ddl-auto=create-drop).
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = UserServiceIntegrationTest.DataSourceInitializer.class)
public class UserServiceIntegrationTest {

    @ClassRule
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("usertestdb")
            .withUsername("usertest")
            .withPassword("userpass");

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
    private UserService userService;

    @Autowired
    private TaskService taskService;

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private User buildUser(String suffix) {
        return new User(
                "user_" + suffix + "@test.com",
                "User " + suffix,
                "password123",       // >= 5 chars requis par @Length
                "images/user.png"
        );
    }

    private Task buildTask(String name) {
        return new Task(name, "Desc " + name,
                LocalDate.now().plusDays(10), false, "TestAdmin");
    }

    // ------------------------------------------------------------------
    // Scénario 1 : Création d'un utilisateur
    // ------------------------------------------------------------------

    /**
     * La création d'un utilisateur doit persister l'entité avec un ID généré
     * et un mot de passe encodé (BCrypt).
     */
    @Test
    public void testCreateUser_shouldPersistWithEncodedPassword() {
        User user = buildUser("create");
        String rawPassword = user.getPassword();

        User saved = userService.createUser(user);

        assertNotNull("L'ID doit être généré", saved.getId());
        assertNotEquals("Le mot de passe doit être encodé (BCrypt)", rawPassword, saved.getPassword());
        assertTrue("Le hash BCrypt doit commencer par $2", saved.getPassword().startsWith("$2"));
    }

    // ------------------------------------------------------------------
    // Scénario 2 : Récupération par e-mail
    // ------------------------------------------------------------------

    /**
     * getUserByEmail() doit retrouver l'utilisateur créé par son e-mail exact.
     */
    @Test
    public void testGetUserByEmail_shouldReturnCorrectUser() {
        User user = buildUser("byemail");
        userService.createUser(user);

        User found = userService.getUserByEmail("user_byemail@test.com");

        assertNotNull("L'utilisateur doit être retrouvé par e-mail", found);
        assertEquals("user_byemail@test.com", found.getEmail());
    }

    // ------------------------------------------------------------------
    // Scénario 3 : Vérification de présence d'un e-mail
    // ------------------------------------------------------------------

    /**
     * isUserEmailPresent() doit retourner vrai pour un e-mail existant
     * et faux pour un e-mail inconnu.
     */
    @Test
    public void testIsUserEmailPresent_existingEmail_shouldReturnTrue() {
        User user = buildUser("emailcheck");
        userService.createUser(user);

        assertTrue("L'e-mail existant doit être détecté",
                userService.isUserEmailPresent("user_emailcheck@test.com"));
        assertFalse("Un e-mail inconnu ne doit pas être détecté",
                userService.isUserEmailPresent("nobody@nowhere.com"));
    }

    // ------------------------------------------------------------------
    // Scénario 4 : Récupération par ID
    // ------------------------------------------------------------------

    /**
     * getUserById() doit retrouver l'utilisateur persisté par son ID.
     */
    @Test
    public void testGetUserById_shouldReturnCorrectUser() {
        User user = buildUser("byid");
        User saved = userService.createUser(user);

        User found = userService.getUserById(saved.getId());

        assertNotNull("L'utilisateur doit être retrouvé par ID", found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("user_byid@test.com", found.getEmail());
    }

    // ------------------------------------------------------------------
    // Scénario 5 : listage de tous les utilisateurs
    // ------------------------------------------------------------------

    /**
     * findAll() doit inclure les utilisateurs créés pendant ce test.
     */
    @Test
    public void testFindAllUsers_shouldContainCreatedUsers() {
        User u1 = userService.createUser(buildUser("listA"));
        User u2 = userService.createUser(buildUser("listB"));

        List<User> all = userService.findAll();

        assertTrue("u1 doit figurer dans la liste",
                all.stream().anyMatch(u -> u.getId().equals(u1.getId())));
        assertTrue("u2 doit figurer dans la liste",
                all.stream().anyMatch(u -> u.getId().equals(u2.getId())));
    }

    // ------------------------------------------------------------------
    // Scénario 6 : Attribution d'une tâche à un utilisateur
    // ------------------------------------------------------------------

    /**
     * assignTaskToUser() doit lier une tâche à un utilisateur,
     * et unassignTask() doit supprimer ce lien.
     * (Nouveau scénario – voir README)
     */
    @Test
    public void testAssignAndUnassignTask() {
        User user = userService.createUser(buildUser("assign"));
        Task task  = buildTask("Tâche assignée");
        taskService.createTask(task);

        // Attribution
        taskService.assignTaskToUser(task, user);
        Task afterAssign = taskService.getTaskById(task.getId());
        assertNotNull("L'owner de la tâche doit être défini", afterAssign.getOwner());
        assertEquals(user.getId(), afterAssign.getOwner().getId());

        // Désattribution
        taskService.unassignTask(afterAssign);
        Task afterUnassign = taskService.getTaskById(task.getId());
        assertNull("L'owner de la tâche doit être null après désattribution",
                afterUnassign.getOwner());
    }
}
