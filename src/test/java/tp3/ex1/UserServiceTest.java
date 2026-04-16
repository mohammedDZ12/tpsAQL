package tp3.ex1;
import org.example.tp3.ex1.User;
import org.example.tp3.ex1.UserRepository;
import org.example.tp3.ex1.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    private UserService userService;
    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }
    @Test
    @DisplayName("getUserById doit appeler findUserById avec le bon ID")
    void testGetUserById_CallsRepositoryWithCorrectId() {
        long userId = 1L;
        User expectedUser = new User(userId, "Alice", "alice@example.com");
        when(userRepository.findUserById(userId)).thenReturn(expectedUser);
        User result = userService.getUserById(userId);
        verify(userRepository, times(1)).findUserById(userId);
        assertEquals(expectedUser, result);
    }
    @Test
    @DisplayName("getUserById doit retourner l'utilisateur correct")
    void testGetUserById_ReturnsCorrectUser() {
        long userId = 42L;
        User expectedUser = new User(userId, "Bob", "bob@example.com");
        when(userRepository.findUserById(userId)).thenReturn(expectedUser);
        User result = userService.getUserById(userId);
        assertNotNull(result);
        assertEquals(42L, result.getId());
        assertEquals("Bob", result.getName());
        assertEquals("bob@example.com", result.getEmail());
    }
    @Test
    @DisplayName("getUserById doit retourner null si l'utilisateur n'existe pas")
    void testGetUserById_ReturnsNullWhenUserNotFound() {
        long userId = 999L;
        when(userRepository.findUserById(userId)).thenReturn(null);
        User result = userService.getUserById(userId);
        assertNull(result);
        verify(userRepository, times(1)).findUserById(userId);
    }
    @Test
    @DisplayName("getUserById ne doit appeler le repository qu'une seule fois")
    void testGetUserById_CallsRepositoryExactlyOnce() {
        long userId = 10L;
        when(userRepository.findUserById(userId)).thenReturn(new User(userId, "Charlie", "charlie@example.com"));
        userService.getUserById(userId);
        verify(userRepository, times(1)).findUserById(userId);
        verify(userRepository, never()).findUserById(99L);
    }
}
