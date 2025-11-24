package infrastructure.http.dto;

import java.time.Instant;

public class UrlResponseDto {
    private String id;
    private String originalUrl;
    private String shortCode;
    private Instant createdAt;

    public UrlResponseDto(String id, String originalUrl, String shortCode, Instant createdAt) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getOriginalUrl() { return originalUrl; }
    public String getShortCode() { return shortCode; }
    public Instant getCreatedAt() { return createdAt; }
}