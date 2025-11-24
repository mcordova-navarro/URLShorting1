package infrastructure.repositories;

import domain.models.Url;
import domain.repositories.UrlRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUrlRepositoryTest {

    @Test
    void saveAndFindByShortCode_persistsUrl() {
        UrlRepository repo = new InMemoryUrlRepository();

        Url url = new Url("1", "https://example.com", "abc123", Instant.now(), "user-1");
        repo.save(url);

        Optional<Url> found = repo.findByShortCode("abc123");
        assertTrue(found.isPresent());
        assertEquals("https://example.com", found.get().getOriginalUrl());
    }

    @Test
    void findByUserId_returnsOnlyMatchingUserUrls() {
        InMemoryUrlRepository repo = new InMemoryUrlRepository();

        repo.save(new Url("1", "https://a.com", "aaa111", Instant.now(), "user-1"));
        repo.save(new Url("2", "https://b.com", "bbb222", Instant.now(), "user-1"));
        repo.save(new Url("3", "https://c.com", "ccc333", Instant.now(), "user-2"));

        List<Url> user1 = repo.findByUserId("user-1");
        assertEquals(2, user1.size());
        assertTrue(user1.stream().allMatch(u -> u.getUserId().equals("user-1")));
    }

    @Test
    void deleteByIdAndUserId_withCorrectOwner_deletes() {
        InMemoryUrlRepository repo = new InMemoryUrlRepository();
        Url url = new Url("1", "https://example.com", "abc123", Instant.now(), "user-1");
        repo.save(url);

        repo.deleteByIdAndUserId("1", "user-1");

        assertTrue(repo.findByShortCode("abc123").isEmpty());
    }

    @Test
    void deleteByIdAndUserId_withWrongOwner_throws() {
        InMemoryUrlRepository repo = new InMemoryUrlRepository();
        Url url = new Url("1", "https://example.com", "abc123", Instant.now(), "user-1");
        repo.save(url);

        assertThrows(IllegalArgumentException.class,
                () -> repo.deleteByIdAndUserId("1", "user-2"));
    }
}
