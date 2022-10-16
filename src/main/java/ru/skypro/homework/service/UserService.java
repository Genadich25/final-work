package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.SiteUser;

public interface UserService {

    ResponseWrapper<UserDto> getUsers();

    UserDto updateUser(UserDto user, String email);

    NewPasswordDto setPassword(NewPasswordDto password, Authentication auth);

    UserDto getUser(Integer id);

    SiteUser findUserByName(String name);


}
