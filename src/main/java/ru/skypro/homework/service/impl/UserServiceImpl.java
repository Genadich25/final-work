package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.repositories.SiteUserRepository;
import ru.skypro.homework.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final SiteUserRepository siteUserRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(SiteUserRepository siteUserRepository, UserMapper userMapper) {
        this.siteUserRepository = siteUserRepository;
        this.userMapper = userMapper;
    }


    @Override
    public CreateUserDto addUser(CreateUserDto user) {
        logger.info("Request for creating user with firstName: {}; lastName: {}; phone: {}; email: {}", user.getFirstName(), user.getLastName(),
                user.getPhone(), user.getEmail());
        SiteUser siteUser = userMapper.fromCreateUserDtoToSiteUser(user);
        return userMapper.fromSiteUserToCreateUserDto(siteUserRepository.save(siteUser));
    }

    @Override
    public ResponseWrapper<UserDto> getUsers() {
        logger.info("Request for getting list of all users");
        List<SiteUser> siteUsers = siteUserRepository.findAll();
        List<UserDto> result = new ArrayList<>();
        for (SiteUser user : siteUsers) {
            result.add(userMapper.fromSiteUserToUserDto(user));
        }
        ResponseWrapper<UserDto> responseWrapperDto = new ResponseWrapper<>();
        responseWrapperDto.setList(result);
        responseWrapperDto.setCount(result.size());
        return responseWrapperDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        logger.info("Request for updating userDto with id: {}", userDto.getId());
        Optional<SiteUser> siteUser = siteUserRepository.findById(userDto.getId());
        if (siteUser.isEmpty()) {
            logger.info("There are not userDto with id {} in list of users", userDto.getId());
            return null;
        } else {
            SiteUser user = userMapper.fromUserDtoToSiteUser(siteUser.get(), userDto);
            user.setPassword(siteUser.get().getPassword());
            logger.info("Changes are finished");
            return userMapper.fromSiteUserToUserDto(siteUserRepository.save(user));
        }
    }

    @Override
    public NewPasswordDto setPassword(NewPasswordDto password) {
        Optional<SiteUser> userOptional = siteUserRepository.findSiteUserByPassword(password.getCurrentPassword());
        if (userOptional.isEmpty()) {
            return null;
        } else {
            SiteUser result = userOptional.get();
            logger.info("Request for change password of user with firstName: {}; lastName: {}", result.getFirstName(), result.getLastName());
            result.setPassword(password.getNewPassword());
            siteUserRepository.save(result);
            return password;
        }
    }

    @Override
    public UserDto getUser(Integer id) {
        logger.info("Request for getting information about user with id {}", id);
        Optional<SiteUser> siteUser = siteUserRepository.findById(id);
        return siteUser.map(userMapper::fromSiteUserToUserDto).orElse(null);
    }

    @Override
    public SiteUser findUserByName(String name) {
        return siteUserRepository.findSiteUserByLastName(name);
    }
}
