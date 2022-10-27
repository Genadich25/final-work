package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
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

    @Operation(summary = "Getting list with info about all users (if role is admin) or info about one user himself (if role is user)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Info is found successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseWrapper.class))),
            })
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<ResponseWrapper<UserDto>> getUsers() {
        ResponseWrapper<UserDto> result = userService.getUsers();
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Updating one existed user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updating is completed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto.class))),
            })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto user) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto result = userService.updateUser(user, email);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Change password of one existed user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Change password is completed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = NewPasswordDto.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Check entered password"
                    )
            })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/set_password")
    public ResponseEntity<NewPasswordDto> setPassword(@RequestBody NewPasswordDto password) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        NewPasswordDto result = userService.setPassword(password, email);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Getting info about one user by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Info is found successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You haven't access to info about this user"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User with this id doesn't exist"
                    )
            })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Integer id) {
        UserDto result = userService.getUser(id);
        return ResponseEntity.ok(result);
    }


}
