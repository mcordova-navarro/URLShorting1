package ru.cft.template.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static ru.cft.template.constants.Headers.AUTHORIZATION_HEADER;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class JwtFilter extends GenericFilterBean {
    JwtTokenProvider jwtTokenProvider;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = ((HttpServletRequest) request).getHeader(AUTHORIZATION_HEADER);

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token != null && jwtTokenProvider.validateToken(token)) {

            try {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            catch (Exception ignored){
                log.info(ignored.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}
