package domain.models;

import java.time.Instant;

public class Url {
    private final String id;              // internal id (UUID)
    private final String originalUrl;     // URL original
    private final String shortCode;       // código corto único (>= 6 chars)
    private final Instant createdAt;      // fecha de creación
    private final String userId;          // usuario que la creó (id de dominio)

    public Url(String id, String originalUrl, String shortCode, Instant createdAt, String userId) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public String getId() { return id; }

    public String getOriginalUrl() { return originalUrl; }

    public String getShortCode() { return shortCode; }

    public Instant getCreatedAt() { return createdAt; }

    public String getUserId() { return userId; }
}