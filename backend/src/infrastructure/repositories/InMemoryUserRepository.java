package infrastructure.repositories;

import domain.models.User;
import domain.repositories.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> usersByUsername = new HashMap<>();

    public InMemoryUserRepository() {
        // Demo user: username=admin, password=admin, role=ADMIN
        User admin = new User("1", "admin", "admin", "ADMIN");
        usersByUsername.put(admin.getUsername(), admin);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }
}
