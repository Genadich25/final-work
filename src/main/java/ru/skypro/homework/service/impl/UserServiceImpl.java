package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.entities.SiteUserDetails;
import ru.skypro.homework.mappers.UserMapper;
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

    public UserServiceImpl(SiteUserRepository siteUserRepository, UserDetailsRepository userDetails, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.siteUserRepository = siteUserRepository;
        this.userDetails = userDetails;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseWrapper<UserDto> getUsers() {
        logger.info("Request for getting list of all users");
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

//    настроить обновление пользователя
    @Override
    public UserDto updateUser(UserDto userDto, String email) {
        logger.info("Request for updating user with userName: {}", email);
        Optional<SiteUser> siteUser = siteUserRepository.findSiteUserByUsername(email);
        if (siteUser.isEmpty()) {
            logger.info("There is not user with username {} in list of users", email);
            return null;
        } else {
            SiteUser user = userMapper.fromUserDtoToSiteUser(siteUser.get(), userDto);
            user.setPassword(siteUser.get().getPassword());
            logger.info("Changes are finished");
            return userMapper.fromSiteUserToUserDto(siteUserRepository.save(user).getSiteUserDetails());
        }
    }

    @Override
    public NewPasswordDto setPassword(NewPasswordDto password, Authentication auth) {
        Optional<SiteUser> userOptional = siteUserRepository.findSiteUserByPassword(passwordEncoder.encode(password.getCurrentPassword()));
        if (userOptional.isEmpty() || !userOptional.get().getUsername().equals(auth.getName())) {
            return null;
        } else {
            SiteUser result = userOptional.get();
            logger.info("Request for change password of user with username: {}", result.getUsername());
            result.setPassword(passwordEncoder.encode(password.getNewPassword()));
            siteUserRepository.save(result);
            return password;
        }
    }

    @Override
    public UserDto getUser(Integer id) {
        logger.info("Request for getting information about user with id {}", id);
        Optional<SiteUserDetails> siteUser = userDetails.findById(id);
        if (siteUser.isEmpty()) {
            return null;
        } else {
            SiteUserDetails user = siteUser.get();
            return userMapper.fromSiteUserToUserDto(user);
        }
    }

    @Override
    public SiteUser findUserByName(String name) {
        return siteUserRepository.findSiteUserByUsername(name).orElseThrow();
    }
}
