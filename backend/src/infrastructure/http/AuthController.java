package infrastructure.http;

import domain.models.User;
import domain.services.AuthService;
import infrastructure.http.dto.LoginRequestDto;
import infrastructure.http.dto.LoginResponseDto;
import infrastructure.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        // Autentica en dominio
        User u = authService.authenticate(request.getUsername(), request.getPassword());

        // Adaptar a UserDetails de Spring Security para generar JWT
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole())
                .build();

        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
