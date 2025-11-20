package ru.cft.template.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.cft.template.entity.Session;
import ru.cft.template.service.SessionService;
import ru.cft.template.service.UserService;

import static ru.cft.template.constants.messages.ServiceMessages.BAD_CREDENTIALS_MESSAGE;


@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthProvider implements AuthenticationProvider {
    CustomUserDetailsService userDetailsService;
    PasswordEncoder passwordEncoder;
    SessionService sessionService;
    JwtTokenProvider jwtTokenProvider;
    UserService userService;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        SessionUser userDetails = (SessionUser) userDetailsService.loadUserByUsername(email);


        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            String accessToken = jwtTokenProvider.createAccessToken(userDetails.getId(), userDetails.getEmail());
            String refreshToken = jwtTokenProvider.createRefreshToken(userDetails.getId(), userDetails.getEmail());

            Session session = sessionService.saveSession(accessToken, refreshToken, userService.getByEmail(email));

            userDetails.setSession(session);

            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
