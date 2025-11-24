package infrastructure.repositories;

import domain.models.Url;
import domain.repositories.UrlRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryUrlRepository implements UrlRepository {

    private final Map<String, Url> store = new ConcurrentHashMap<>();

    @Override
    public Url save(Url url) {
        store.put(url.getId(), url);
        return url;
    }

    @Override
    public Optional<Url> findByShortCode(String shortCode) {
        return store.values()
                .stream()
                .filter(u -> u.getShortCode().equals(shortCode))
                .findFirst();
    }

    @Override
    public List<Url> findByUserId(String userId) {
        return store.values()
                .stream()
                .filter(u -> u.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByIdAndUserId(String id, String userId) {
        Url existing = store.get(id);
        if (existing != null && existing.getUserId().equals(userId)) {
            store.remove(id);
        } else {
            throw new IllegalArgumentException("URL not found or not owned by user");
        }
    }
}