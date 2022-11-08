package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.skypro.homework.constants.UserConstants;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.repositories.SiteUserRepository;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
class AuthServiceImplTest {

    @Autowired
    private AuthServiceImpl authService;

    @MockBean
    private UserDetailsManager detailsManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private SiteUserRepository userRepository;

    @Test
    void login() {
        String username = "user";
        String password = "password";
        String encryptedPassword = "password";
        UserDetails userDetails = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getPassword() {
                return password;
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        };
        Mockito.when(detailsManager.userExists(username)).thenReturn(true);
        Mockito.when(detailsManager.loadUserByUsername(username)).thenReturn(userDetails);
        Mockito.when(passwordEncoder.matches(password, encryptedPassword)).thenReturn(true);
        boolean login = authService.login("user", "password");
        assertTrue(login);
    }

    @Test
    void loginForNotExistedUser() {
        String username = "username";
        String password = "password";
        Mockito.when(detailsManager.userExists(username)).thenReturn(false);
        boolean login = authService.login(username, password);
        assertFalse(login);
    }

    @Test
    void loginWithIncorrectPassword() {
        String username = "user";
        String password = "password";
        String encryptedPassword = "password";
        UserDetails userDetails = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getPassword() {
                return password;
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        };
        Mockito.when(detailsManager.userExists(username)).thenReturn(true);
        Mockito.when(detailsManager.loadUserByUsername(username)).thenReturn(userDetails);
        Mockito.when(passwordEncoder.matches(password, encryptedPassword)).thenReturn(false);
        boolean login = authService.login("user", "password");
        assertFalse(login);
    }


    @Test
    void register() {
        SiteUser siteUser = new SiteUser();
        String username = "username";
        RegisterReq req = UserConstants.createReq("username", "password", "John", "Fray", "33-98-75", Role.USER);
        Role role = Role.USER;
        siteUser.setUsername(req.getUsername());
        siteUser.setPassword(req.getPassword());
        Mockito.when(detailsManager.userExists(username)).thenReturn(false);
        Mockito.when(passwordEncoder.encode(req.getPassword())).thenReturn("password");
        Mockito.when(userRepository.findById(req.getUsername())).thenReturn(Optional.of(siteUser));
        assertTrue(authService.register(req, role));
    }

    @Test
    void registerExistedUser() {
        String username = "username";
        RegisterReq req = UserConstants.createReq("username", "password", "John", "Fray", "33-98-75", Role.USER);
        Role role = Role.USER;
        Mockito.when(detailsManager.userExists(username)).thenReturn(true);
        assertFalse(authService.register(req, role));
    }

}