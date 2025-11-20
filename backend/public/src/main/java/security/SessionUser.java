package ru.cft.template.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.cft.template.entity.Session;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class SessionUser implements UserDetails {

    private final UUID id;
    private Session session;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String patronymic;
    private final String password;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }


}
