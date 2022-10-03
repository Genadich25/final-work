package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CreateUser;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    @Override
    public CreateUser addUser(CreateUser user) {
        CreateUser createUser = new CreateUser();
        createUser.setFirstName(user.getFirstName());
        createUser.setLastName(user.getLastName());
        createUser.setEmail(user.getEmail());
        createUser.setPhone(user.getPhone());
        createUser.setPassword(user.getPassword());
        return createUser;
    }

    @Override
    public ResponseWrapper<User> getUsers() {
        List<User> list = new ArrayList<>();
        list.add(new User());
        Integer count = list.size();
        ResponseWrapper<User> response = new ResponseWrapper<>();
        response.setCount(count);
        response.setList(list);
        return response;
    }

    @Override
    public User updateUser(User user) {
        return user;
    }

    @Override
    public NewPassword setPassword(NewPassword password) {
        return password;
    }

    @Override
    public User getUser(Integer id) {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Cage");
        user.setPhone("145");
        user.setEmail("johnyCage@gmail.com");
        user.setId(id);
        return user;
    }

    @Override
    public User findUserByName(String name) {
        return null;
    }
}
