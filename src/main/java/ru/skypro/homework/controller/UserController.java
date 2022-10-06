package ru.skypro.homework.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.UserDto;
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
    public ResponseEntity<CreateUserDto> addUser(@RequestBody CreateUserDto createUser) {
        if (createUser.getEmail() == null || createUser.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        CreateUserDto createUserDto = userService.addUser(createUser);
        return ResponseEntity.ok(createUserDto);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseWrapper<UserDto>> getUsers() {
        ResponseWrapper<UserDto> result = userService.getUsers();
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto user) {
        UserDto result = userService.updateUser(user);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/set_password")
    public ResponseEntity<NewPasswordDto> setPassword(@RequestBody NewPasswordDto password) {
        NewPasswordDto result = userService.setPassword(password);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Integer id) {
        UserDto result = userService.getUser(id);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(result);
    }


}
