package infrastructure.http;

import domain.models.Url;
import domain.services.UrlService;
import infrastructure.http.dto.UrlRequestDto;
import infrastructure.http.dto.UrlResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    // POST /api/urls  -> crear URL corta
    @PostMapping
    public ResponseEntity<UrlResponseDto> create(@RequestBody UrlRequestDto request, Principal principal) {
        String userId = principal.getName(); // viene del JWT
        Url url = urlService.createShortUrl(request.getOriginalUrl(), userId);
        return ResponseEntity.ok(toDto(url));
    }

    // GET /api/urls -> listar links del usuario autenticado
    @GetMapping
    public ResponseEntity<List<UrlResponseDto>> list(Principal principal) {
        String userId = principal.getName();
        List<UrlResponseDto> urls = urlService.listByUser(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(urls);
    }

    // DELETE /api/urls/{id} -> eliminar link
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Principal principal) {
        String userId = principal.getName();
        urlService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    private UrlResponseDto toDto(Url url) {
        return new UrlResponseDto(
                url.getId(),
                url.getOriginalUrl(),
                url.getShortCode(),
                url.getCreatedAt()
        );
    }
}