package ru.skypro.homework.service;

import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.UserDto;

/**
 * Interface including methods for working with entities site user and site user details
 */
public interface UserService {

    /**
     * Method for getting list of users
     *
     * @return ResponseWrapperDto<UserDto></UserDto>
     */
    ResponseWrapper<UserDto> getUsers(String email);


    /**
     * Method for updating data of one authorized user
     *
     * @return UserDto
     */
    UserDto updateUser(UserDto user, String email);


    /**
     * Method for change password of one authorized user
     *
     * @return NewPasswordDto
     */
    NewPasswordDto setPassword(NewPasswordDto password, String email);

    /**
     * Method for getting info about one user by id
     *
     * @return UserDto
     */
    UserDto getUser(Integer id, String email);


}
