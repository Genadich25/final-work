package ru.skypro.homework.controller;


import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.service.impl.AuthServiceImpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @MockBean
    private AuthServiceImpl authService;

    @Autowired
    private MockMvc mockMvc;

    private final JSONObject loginObject = new JSONObject();

    private final JSONObject registerObject = new JSONObject();


    @BeforeEach
    void setUp() throws Exception {
        loginObject.put("username", "fox_news@gmail.com");
        loginObject.put("password", "ticket");

        registerObject.put("username", "vinnie_puh@gmail.com");
        registerObject.put("password", "honey");
        registerObject.put("firstName", "Alan");
        registerObject.put("lastName", "Milne");
        registerObject.put("phone", "32-31-33");
        registerObject.put("role", "USER");
    }

    @Test
    public void checkController() {
        assertThat(authController).isNotNull();

    }

    @Test
    void login() throws Exception {
        when(authService.login(any(String.class), any(String.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content(loginObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void UnsuccessfulLogin() throws Exception {
        when(authService.login(any(String.class), any(String.class))).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content(loginObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Login is forbidden. Check input username and password"));
    }


    @Test
    void register() throws Exception {
        when(authService.register(any(RegisterReq.class), any(Role.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register")
                        .content(registerObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void UnsuccessfulRegister() throws Exception {
        when(authService.register(any(RegisterReq.class), any(Role.class))).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register")
                        .content(registerObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User with this email already exists. Check input email"));
    }





}