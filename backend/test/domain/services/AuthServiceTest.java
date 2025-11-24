package domain.services;

import domain.models.User;
import domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private static class InMemoryUserRepositoryStub implements UserRepository {
        private final User user;

        InMemoryUserRepositoryStub(User user) {
            this.user = user;
        }

        @Override
        public Optional<User> findByUsername(String username) {
            if (user != null && user.getUsername().equals(username)) {
                return Optional.of(user);
            }
            return Optional.empty();
        }
    }

    @Test
    void authenticate_withValidCredentials_returnsUser() {
        User demo = new User("1", "alice", "secret", "USER");
        UserRepository repo = new InMemoryUserRepositoryStub(demo);
        AuthService authService = new AuthService(repo);

        User result = authService.authenticate("alice", "secret");

        assertEquals("alice", result.getUsername());
        assertEquals("USER", result.getRole());
    }

    @Test
    void authenticate_withWrongPassword_throws() {
        User demo = new User("1", "alice", "secret", "USER");
        UserRepository repo = new InMemoryUserRepositoryStub(demo);
        AuthService authService = new AuthService(repo);

        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("alice", "wrong"));
    }

    @Test
    void authenticate_withUnknownUser_throws() {
        UserRepository repo = new InMemoryUserRepositoryStub(null);
        AuthService authService = new AuthService(repo);

        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("bob", "anything"));
    }
}
