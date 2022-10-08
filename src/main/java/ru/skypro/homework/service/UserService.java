package ru.skypro.homework.service;

import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.SiteUser;

public interface UserService {

    CreateUserDto addUser(CreateUserDto user);

    ResponseWrapper<UserDto> getUsers();

    UserDto updateUser(UserDto user);

    NewPasswordDto setPassword(NewPasswordDto password);

    UserDto getUser(Integer id);

    SiteUser findUserByName(String name);


}
