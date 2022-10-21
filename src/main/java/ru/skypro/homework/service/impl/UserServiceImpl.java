package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.entities.SiteUserDetails;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.repositories.AuthorityRepository;
import ru.skypro.homework.repositories.SiteUserRepository;
import ru.skypro.homework.repositories.UserDetailsRepository;
import ru.skypro.homework.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final SiteUserRepository siteUserRepository;
    private final UserDetailsRepository userDetails;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    public UserServiceImpl(SiteUserRepository siteUserRepository, UserDetailsRepository userDetails, UserMapper userMapper,
                           PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.siteUserRepository = siteUserRepository;
        this.userDetails = userDetails;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public ResponseWrapper<UserDto> getUsers() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = authorityRepository.findAuthorityByUsername(email).getAuthority();
        logger.info("Request for getting list of all users from userName: {}, with role: {}", email, role);
        List<SiteUserDetails> siteUsers = userDetails.findAll();
        List<UserDto> result = new ArrayList<>();
        for (SiteUserDetails user : siteUsers) {
            result.add(userMapper.fromSiteUserToUserDto(user));
        }
        ResponseWrapper<UserDto> responseWrapperDto = new ResponseWrapper<>();
        responseWrapperDto.setList(result);
        responseWrapperDto.setCount(result.size());
        return responseWrapperDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto, String email) {
        logger.info("Request for updating user with userName: {}", email);
        Optional<SiteUser> siteUser = siteUserRepository.findSiteUserByUsername(email);
        if (siteUser.isEmpty()) {
            logger.info("There is not user with username {} in list of users", email);
            return null;
        } else {
            SiteUser user = userMapper.fromUserDtoToSiteUser(siteUser.get(), userDto);
            logger.info("Changes are finished");
            return userMapper.fromSiteUserToUserDto(siteUserRepository.save(user).getSiteUserDetails());
        }
    }

    @Override
    public NewPasswordDto setPassword(NewPasswordDto password, Authentication auth) {
        Optional<SiteUser> userOptional = siteUserRepository.findSiteUserByUsername(auth.getName());
        if (userOptional.isEmpty()) {
            logger.info("Пользователя с таким email: *** \"{}\" *** нет.", auth.getName());
            return null;
        } else {
            SiteUser result = userOptional.get();
            logger.info("Request for change password of user: \"{}\" from email: {}", result.getUsername(), auth.getName());
            if (!passwordEncoder.matches(password.getCurrentPassword(), result.getPassword())) {
                logger.info("Введеный пароль: {} не соответствует текущему паролю: {}. Изменение пароля запрещено", password.getCurrentPassword(), result.getPassword());
                return null;
            } else {
                logger.info("Введеный пароль и текущий пароль совпадают. Изменение пароля допускается.");
                result.setPassword(passwordEncoder.encode(password.getNewPassword()));
                siteUserRepository.save(result);
                return password;
            }
        }
    }

    @Override
    public UserDto getUser(Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = authorityRepository.findAuthorityByUsername(email).getAuthority();
        logger.info("Request for getting information about user with id {} from userName: {}", id, email);
        Optional<SiteUserDetails> siteUser = userDetails.findById(id);
        if (siteUser.isEmpty()) {
            return null;
        } else {
            if ((siteUser.get().getSiteUser().getUsername().equals(email) && role.equals("ROLE_USER")) || role.equals("ROLE_ADMIN")) {
                return userMapper.fromSiteUserToUserDto(siteUser.get());
            } else {
                UserDto userDto = new UserDto();
                userDto.setFirstName("Not access");
                return userDto;
            }
        }
    }

    @Override
    public SiteUser findUserByName(String name) {
        return siteUserRepository.findSiteUserByUsername(name).orElseThrow();
    }
}
