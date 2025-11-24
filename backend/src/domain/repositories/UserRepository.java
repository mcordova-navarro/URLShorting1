package domain.repositories;

import domain.models.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
}
