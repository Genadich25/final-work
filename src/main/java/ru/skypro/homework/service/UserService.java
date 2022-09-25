package ru.skypro.homework.service;

import ru.skypro.homework.dto.CreateUser;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.User;

public interface UserService {

    CreateUser addUser(CreateUser user);

    ResponseWrapper<User> getUsers();

    User updateUser(User user);

    NewPassword setPassword(NewPassword password);

    User getUser(Integer id);


}
