package ru.skypro.homework.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.constants.UserConstants;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.exceptionsHandler.exceptions.IncorrectPasswordException;
import ru.skypro.homework.exceptionsHandler.exceptions.NotAccessException;
import ru.skypro.homework.exceptionsHandler.exceptions.UserNotFoundException;
import ru.skypro.homework.service.impl.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {


    @InjectMocks
    private UserController userController;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void checkController() {
        assertThat(userController).isNotNull();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersForRoleAdmin() throws Exception {
        ResponseWrapper<UserDto> users = new ResponseWrapper<>();
        List<UserDto> list = new ArrayList<>();
        list.add(UserConstants.createUserDto(1, "johny@gmail.com", "John", "Cage", "33-12-96"));
        list.add(UserConstants.createUserDto(2, "ste_Ge@gmail.com", "Steven", "Peterson", "55-78-20"));
        list.add(UserConstants.createUserDto(3, "fray@gmail.com", "Sebastian", "Fray", "14-30-73"));
        users.setResults(list);
        users.setCount(list.size());

        when(userService.getUsers(any(String.class))).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value(list.size()));
    }

    @Test
    @WithMockUser
    void getUsersForRoleUser() throws Exception {
        ResponseWrapper<UserDto> users = new ResponseWrapper<>();
        List<UserDto> list = new ArrayList<>();
        list.add(UserConstants.createUserDto(1, "johny@gmail.com", "John", "Cage", "33-12-96"));
        users.setResults(list);
        users.setCount(list.size());

        when(userService.getUsers(any(String.class))).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value(list.size()));
    }


    @Test
    @WithMockUser
    void updateUser() throws Exception {
        UserDto result = UserConstants.createUserDto(1, "johny@gmail.com", "John", "Cage", "33-12-96");
        JSONObject inputUser = new JSONObject();
        inputUser.put("username", "johny@gmail.com");
        inputUser.put("firstName", "John");
        inputUser.put("lastName", "Cage");

        when(userService.updateUser(any(UserDto.class), any(String.class))).thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/me")
                        .content(inputUser.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("johny@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Cage"));
    }

    @Test
    @WithMockUser
    void setPassword() throws Exception {
        NewPasswordDto passwordDto = new NewPasswordDto();
        passwordDto.setCurrentPassword("prime_time");
        passwordDto.setNewPassword("erny_mc");

        JSONObject newPasswordObject = new JSONObject();
        newPasswordObject.put("currentPassword", "prime_time");
        newPasswordObject.put("newPassword", "erny_mc");

        when(userService.setPassword(any(NewPasswordDto.class), any(String.class))).thenReturn(passwordDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/set_password")
                        .content(newPasswordObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentPassword").value("prime_time"))
                .andExpect(jsonPath("$.newPassword").value("erny_mc"));
    }

    @Test
    @WithMockUser
    void setPasswordThrowException() throws Exception {

        JSONObject newPasswordObject = new JSONObject();
        newPasswordObject.put("currentPassword", "gf_tr97");
        newPasswordObject.put("newPassword", "smitty");

        when(userService.setPassword(any(NewPasswordDto.class), any(String.class))).thenThrow(new IncorrectPasswordException());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/set_password")
                        .content(newPasswordObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Check entered password"));
    }


    @Test
    @WithMockUser
    void getUser() throws Exception {
        UserDto userDto = UserConstants.createUserDto(1, "some_email@mail.ru", "Steven", "Johnson", "21-98-74");
        when(userService.getUser(any(Integer.class), any(String.class))).thenReturn(userDto);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Steven"));
    }

    @Test
    @WithMockUser
    void getUserThrowNotFoundException() throws Exception {
        when(userService.getUser(any(Integer.class), any(String.class))).thenThrow(new UserNotFoundException());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User with this id not found"));
    }

    @Test
    @WithMockUser
    void getUserThrowNotAccessException() throws Exception {
        when(userService.getUser(any(Integer.class), any(String.class))).thenThrow(new NotAccessException());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/1"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("You haven't access to this information"));
    }

}