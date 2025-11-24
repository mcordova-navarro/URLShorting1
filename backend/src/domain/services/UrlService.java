package domain.services;

import domain.models.Url;
import domain.repositories.UrlRepository;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    // 1) Crear URL corta para usuario autenticado
    public Url createShortUrl(String originalUrl, String userId) {
        validateUrl(originalUrl);

        String shortCode = generateUniqueShortCode(originalUrl);
        Instant now = Instant.now();
        String id = UUID.randomUUID().toString();

        Url url = new Url(id, originalUrl, shortCode, now, userId);
        return urlRepository.save(url);
    }

    // 2) Obtener URL original por shortCode (para redirección)
    public Optional<Url> getByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    // 3) Listar URLs de un usuario
    public List<Url> listByUser(String userId) {
        return urlRepository.findByUserId(userId);
    }

    // 4) Eliminar URL (solo si pertenece al usuario)
    public void delete(String id, String userId) {
        urlRepository.deleteByIdAndUserId(id, userId);
    }

    // --- Reglas de dominio / helpers ---

    private void validateUrl(String originalUrl) {
        try {
            new URL(originalUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL");
        }
    }

    private String generateUniqueShortCode(String originalUrl) {
        String base = Integer.toHexString(originalUrl.hashCode());
        if (base.length() < 6) {
            base = (base + "000000").substring(0, 6);
        }
        return base.substring(0, 6);
    }
}