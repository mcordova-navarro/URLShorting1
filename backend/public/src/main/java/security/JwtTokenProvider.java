package ru.cft.template.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.cft.template.constants.messages.ServiceMessages;
import ru.cft.template.dto.session.TokenResponse;
import ru.cft.template.entity.Session;
import ru.cft.template.entity.User;
import ru.cft.template.security.props.JwtProperties;
import ru.cft.template.service.SessionService;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private Key key;
    private final JwtProperties jwtProperties;
    private final SessionService sessionService;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String createAccessToken(UUID userId, String email) {

        Date now = new Date();

        return Jwts.builder()
                .setSubject(email)
                .claim("id", userId.toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtProperties.getAccess()))
                .signWith(key)
                .compact();

    }

    public String createRefreshToken(UUID userId, String email) {
        Date now = new Date();

        return Jwts.builder()
               .claim("id", userId.toString())
               .claim("email", email)
               .setIssuedAt(now)
               .setExpiration(new Date(now.getTime() + jwtProperties.getRefresh()))
               .signWith(key)
               .compact();
    }

    public TokenResponse refreshUserTokens(String refreshToken, User user) {
        TokenResponse tokenResponse = new TokenResponse();

        if (!validateToken(refreshToken)) {
            throw new AccessDeniedException(ServiceMessages.ACCESS_DENIED_MESSAGE);
        }

        UUID userId = user.getId();
        String email = user.getEmail();

        tokenResponse.setUserId(userId);
        tokenResponse.setEmail(email);
        tokenResponse.setAccessToken(createAccessToken(userId, email));
        tokenResponse.setRefreshToken(createRefreshToken(userId, email));

        return tokenResponse;
    }


    public String getId(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return claims.getBody().getId();
    }

    public Date getExpirationDate(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return claims.getBody().getExpiration();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return claims.getBody().getExpiration().after(new Date());
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return false;
        }
    }

    private String extractEmail(String token) {
        return Jwts.parserBuilder()
               .setSigningKey(key)
               .build()
               .parseClaimsJws(token)
               .getBody()
               .getSubject();
    }

    public Authentication getAuthentication(String token) {
        Session session = sessionService.getSessionByAccessToken(token);

        if (session == null) {
            throw new AccessDeniedException(ServiceMessages.ACCESS_DENIED_MESSAGE);
        }

        UserDetails userDetails = SessionUserFactory.create(session.getUser(), session);
        log.info(session.getUser().getEmail());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
