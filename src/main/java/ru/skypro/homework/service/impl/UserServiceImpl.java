package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.entities.SiteUserDetails;
import ru.skypro.homework.exceptionsHandler.exceptions.IncorrectPasswordException;
import ru.skypro.homework.exceptionsHandler.exceptions.NotAccessException;
import ru.skypro.homework.exceptionsHandler.exceptions.UserNotFoundException;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.repositories.AuthorityRepository;
import ru.skypro.homework.repositories.SiteUserRepository;
import ru.skypro.homework.repositories.UserDetailsRepository;
import ru.skypro.homework.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class implements methods for working with entity site user and site user details
 */
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


//    Method for getting list of users
    @Override
    public ResponseWrapper<UserDto> getUsers() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = authorityRepository.findAuthorityByUsername(email).getAuthority();
        logger.info("Request for getting list of all users from userName: {}, with role: {}", email, role);
        List<UserDto> siteUsers = userDetails.findAll().stream().map(userMapper::fromSiteUserToUserDto).collect(Collectors.toList());
        if (role.equals("ROLE_USER")) {
            siteUsers = userDetails.findSiteUserDetailsBySiteUserUsername(email).stream().map(userMapper::fromSiteUserToUserDto).collect(Collectors.toList());
        }
        ResponseWrapper<UserDto> responseWrapperDto = new ResponseWrapper<>();
        responseWrapperDto.setResults(siteUsers);
        responseWrapperDto.setCount(siteUsers.size());
        return responseWrapperDto;
    }



//    Method for updating data of one authorized user
    @Override
    public UserDto updateUser(UserDto userDto, String email) {
        String role = authorityRepository.findAuthorityByUsername(email).getAuthority();
        logger.info("Request for updating user with username: {}, with role: {}", email, role);
        SiteUser siteUser = siteUserRepository.findByUsername(email);
        SiteUser user = userMapper.fromUserDtoToSiteUser(siteUser, userDto);
        logger.info("Changes are finished");
        SiteUser result = siteUserRepository.save(user);
        return userMapper.fromSiteUserToUserDto(result.getSiteUserDetails());
    }



//     Method for change password of one authorized user
    @Override
    public NewPasswordDto setPassword(NewPasswordDto password, String email) {
        SiteUser result = siteUserRepository.findByUsername(email);
        logger.info("Request for change password of user: \"{}\"", email);
        if (!passwordEncoder.matches(password.getCurrentPassword(), result.getPassword())) {
            logger.info("Введеный пароль: {} не соответствует текущему паролю: {}. Изменение пароля запрещено", password.getCurrentPassword(), result.getPassword());
            throw new IncorrectPasswordException();
        } else {
            logger.info("Введеный пароль и текущий пароль совпадают. Изменение пароля разрешено");
            result.setPassword(passwordEncoder.encode(password.getNewPassword()));
            siteUserRepository.save(result);
            return password;
        }
    }


//    Method for getting info about one user by id
    @Override
    public UserDto getUser(Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = authorityRepository.findAuthorityByUsername(email).getAuthority();
        logger.info("Request for getting information about user with id {} from userName: {}", id, email);
        Optional<SiteUserDetails> siteUser = userDetails.findById(id);
        if (siteUser.isEmpty()) {
            throw new UserNotFoundException();
        } else {
            if (!siteUser.get().getSiteUser().getUsername().equals(email) && role.equals("ROLE_USER")) {
                throw new NotAccessException();
            } else {
                return userMapper.fromSiteUserToUserDto(siteUser.get());
            }
        }
    }




}
