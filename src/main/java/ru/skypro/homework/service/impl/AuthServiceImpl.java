package ru.skypro.homework.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.entities.SiteUserDetails;
import ru.skypro.homework.repositories.SiteUserRepository;
import ru.skypro.homework.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final SiteUserRepository siteUserRepository;

    public AuthServiceImpl(UserDetailsManager manager, SiteUserRepository siteUserRepository) {
        this.manager = manager;
        this.siteUserRepository = siteUserRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean login(String userName, String password) {
        if (!manager.userExists(userName)) {
            return false;
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        String encryptedPassword = userDetails.getPassword();
        return passwordEncoder.matches(password, encryptedPassword);
    }

    @Override
    public boolean register(RegisterReq registerReq, Role role) {
        if (manager.userExists(registerReq.getUsername())) {
            return false;
        } else {
            manager.createUser(
                    User.withUsername(registerReq.getUsername())
                            .password(passwordEncoder.encode(registerReq.getPassword()))
                            .roles(role.toString())
                            .build()
            );

            SiteUser siteUser = siteUserRepository.findSiteUserByUsername(registerReq.getUsername()).orElseThrow();
            SiteUserDetails userDetails = new SiteUserDetails();
            userDetails.setFirstName(registerReq.getFirstName());
            userDetails.setLastName(registerReq.getLastName());
            userDetails.setPhone(registerReq.getPhone());
            siteUser.setSiteUserDetails(userDetails);
            siteUserRepository.save(siteUser);
            return true;
        }
    }
}
