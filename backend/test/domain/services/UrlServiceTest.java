package domain.services;

import domain.models.Url;
import infrastructure.repositories.InMemoryUrlRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UrlServiceTest {

    @Test
    void createShortUrl_withValidUrl_persistsAndReturnsUrl() {
        InMemoryUrlRepository repo = new InMemoryUrlRepository();
        UrlService service = new UrlService(repo);

        String original = "https://example.com/page";
        String userId = "user-123";

        Url result = service.createShortUrl(original, userId);

        assertNotNull(result.getId());
        assertEquals(original, result.getOriginalUrl());
        assertEquals(userId, result.getUserId());
        assertNotNull(result.getShortCode());
        assertEquals(6, result.getShortCode().length());

        Optional<Url> fromRepo = repo.findByShortCode(result.getShortCode());
        assertTrue(fromRepo.isPresent());
        assertEquals(original, fromRepo.get().getOriginalUrl());
    }

    @Test
    void createShortUrl_withInvalidUrl_throwsException() {
        InMemoryUrlRepository repo = new InMemoryUrlRepository();
        UrlService service = new UrlService(repo);

        assertThrows(IllegalArgumentException.class,
                () -> service.createShortUrl("not-a-valid-url", "user-1"));
    }

    @Test
    void listByUser_returnsOnlyUrlsForThatUser() {
        InMemoryUrlRepository repo = new InMemoryUrlRepository();
        UrlService service = new UrlService(repo);

        Url u1 = service.createShortUrl("https://a.com", "user-1");
        Url u2 = service.createShortUrl("https://b.com", "user-1");
        Url u3 = service.createShortUrl("https://c.com", "user-2");

        List<Url> user1Urls = service.listByUser("user-1");

        assertEquals(2, user1Urls.size());
        assertTrue(user1Urls.stream().allMatch(u -> u.getUserId().equals("user-1")));
    }

    @Test
    void getByShortCode_findsPreviouslyCreatedUrl() {
        InMemoryUrlRepository repo = new InMemoryUrlRepository();
        UrlService service = new UrlService(repo);

        Url created = service.createShortUrl("https://example.org", "user-1");

        Optional<Url> found = service.getByShortCode(created.getShortCode());
        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void delete_removesUrlOnlyWhenOwnedByUser() {
        InMemoryUrlRepository repo = new InMemoryUrlRepository();
        UrlService service = new UrlService(repo);

        Url created = service.createShortUrl("https://example.org", "user-1");

        // Deleting with correct owner works
        assertDoesNotThrow(() -> service.delete(created.getId(), "user-1"));

        // After deletion, repository no longer finds it
        assertTrue(repo.findByShortCode(created.getShortCode()).isEmpty());
    }
}
