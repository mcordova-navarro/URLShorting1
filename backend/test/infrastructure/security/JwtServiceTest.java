package infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void generateToken_andValidateToken_usingSameUserDetails() {
        JwtService jwtService = new JwtService();

        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
        UserDetails userDetails = new User("alice", "password", authorities);

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        assertTrue(jwtService.isTokenValid(token, userDetails));

        String username = jwtService.extractUsername(token);
        assertEquals("alice", username);
    }
}
