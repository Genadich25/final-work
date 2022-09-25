package ru.skypro.homework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CreateUser;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(value = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CreateUser> addUser(@RequestBody CreateUser createUser) {
        CreateUser user = userService.addUser(createUser);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseWrapper<User>> getUsers() {
        ResponseWrapper<User> result = userService.getUsers();
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/me")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User result = userService.updateUser(user);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/set_password")
    public ResponseEntity<NewPassword> setPassword(@RequestBody NewPassword password) {
        NewPassword result = userService.setPassword(password);
        return ResponseEntity.ok(result);
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        User result = userService.getUser(id);
        return ResponseEntity.ok(result);
    }


}
