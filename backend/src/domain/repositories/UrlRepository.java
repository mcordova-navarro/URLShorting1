package domain.repositories;

import domain.models.Url;
import java.util.List;
import java.util.Optional;

public interface UrlRepository {
    Url save(Url url);

    Optional<Url> findByShortCode(String shortCode);

    List<Url> findByUserId(String userId);

    void deleteByIdAndUserId(String id, String userId);
}