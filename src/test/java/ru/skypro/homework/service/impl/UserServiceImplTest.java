package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.constants.UserConstants;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.Authority;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.entities.SiteUserDetails;
import ru.skypro.homework.exceptionsHandler.exceptions.IncorrectPasswordException;
import ru.skypro.homework.exceptionsHandler.exceptions.NotAccessException;
import ru.skypro.homework.exceptionsHandler.exceptions.UserNotFoundException;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.repositories.AuthorityRepository;
import ru.skypro.homework.repositories.SiteUserRepository;
import ru.skypro.homework.repositories.UserDetailsRepository;
import ru.skypro.homework.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserService out;

    private final SiteUserRepository siteUserRepositoryMock = mock(SiteUserRepository.class);

    private final UserDetailsRepository detailsRepositoryMock = mock(UserDetailsRepository.class);

    private final AuthorityRepository authorityRepositoryMock = mock(AuthorityRepository.class);

    private final UserMapper userMapperMock = mock(UserMapper.class);

    private final PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);

    List<SiteUserDetails> list = new ArrayList<>();
    List<SiteUserDetails> lonelyList = new ArrayList<>();


    @BeforeEach
    void initOut() {
        out = new UserServiceImpl(siteUserRepositoryMock, detailsRepositoryMock, userMapperMock, passwordEncoderMock, authorityRepositoryMock);
    }


    @BeforeEach
    void setUp() {
        list.add(UserConstants.userDetails(1, "John", "Cage", "35-47-96"));
        list.add(UserConstants.userDetails(2, "Bob", "Travis", "22-03-64"));
        list.add(UserConstants.userDetails(3, "Bred", "Jackson", "34-87-60"));

        lonelyList.add(UserConstants.userDetails(1, "John", "Cage", "35-47-96"));
    }

    @Test
    void getUsersForRoleUser() {

        String email = "username";

        Authority authority = new Authority();
        authority.setUsername("username");
        authority.setAuthority("ROLE_USER");

        Mockito.when(authorityRepositoryMock.findAuthorityByUsername(email)).thenReturn(authority);
        Mockito.when(detailsRepositoryMock.findAll()).thenReturn(list);
        Mockito.when(detailsRepositoryMock.findSiteUserDetailsBySiteUserUsername(email)).thenReturn(lonelyList);

        ResponseWrapper<UserDto> result = out.getUsers(email);
        assertEquals(result.getCount(), lonelyList.size());
    }


    @Test
    void getUsersForRoleAdmin() {

        String email = "username";

        Authority authority = new Authority();
        authority.setUsername("username");
        authority.setAuthority("ROLE_ADMIN");

        Mockito.when(authorityRepositoryMock.findAuthorityByUsername(email)).thenReturn(authority);
        Mockito.when(detailsRepositoryMock.findAll()).thenReturn(list);
        Mockito.when(detailsRepositoryMock.findSiteUserDetailsBySiteUserUsername(email)).thenReturn(lonelyList);

        ResponseWrapper<UserDto> result = out.getUsers(email);
        assertEquals(result.getCount(), list.size());
    }

    @Test
    void updateUser() {
        SiteUser siteUser = UserConstants.siteUser("username", "password");
        SiteUserDetails userDetails = UserConstants.userDetails(1, "John", "Cage", "33-12-96");
        siteUser.setSiteUserDetails(userDetails);

        UserDto inputUser = new UserDto();
        inputUser.setLastName("Johnson");
        inputUser.setFirstName("Franky");
        inputUser.setPhone("22-11-33");

        Authority authority = new Authority();
        authority.setUsername("username");
        authority.setAuthority("ROLE_USER");


        UserDto userDto = UserConstants.createUserDto(1, "username", "Franky", "Johnson", "22-11-33");

        SiteUser user = UserConstants.siteUser("username", "password");
        SiteUserDetails resultDetails = UserConstants.userDetails(1, "Franky", "Johnson", "22-11-33");
        user.setSiteUserDetails(resultDetails);

        Mockito.when(authorityRepositoryMock.findAuthorityByUsername(any(String.class))).thenReturn(authority);
        Mockito.when(siteUserRepositoryMock.findByUsername(any(String.class))).thenReturn(siteUser);
        Mockito.when(userMapperMock.fromUserDtoToSiteUser(siteUser, inputUser)).thenReturn(user);
        Mockito.when(siteUserRepositoryMock.save(any(SiteUser.class))).thenReturn(user);
        Mockito.when(userMapperMock.fromSiteUserToUserDto(user.getSiteUserDetails())).thenReturn(userDto);

        UserDto result = out.updateUser(inputUser, "username");
        assertEquals(result.getFirstName(), "Franky");
        assertEquals(result.getLastName(), "Johnson");
        assertEquals(result.getPhone(), "22-11-33");
        assertEquals(result.getId(), 1);
    }


    @Test
    void setPasswordTest() {
        NewPasswordDto passwordDto = new NewPasswordDto();
        passwordDto.setNewPassword("newPassword");
        passwordDto.setCurrentPassword("password");

        SiteUser user = UserConstants.siteUser("username", "password");

        Mockito.when(siteUserRepositoryMock.findByUsername(any(String.class))).thenReturn(user);

        Mockito.when(passwordEncoderMock.matches(any(CharSequence.class), any(String.class))).thenReturn(true);

        Mockito.when(passwordEncoderMock.encode(any(CharSequence.class))).thenReturn("smitty_password");

        NewPasswordDto result = out.setPassword(passwordDto, "username");

        assertEquals("password", result.getCurrentPassword());
        assertEquals("newPassword", result.getNewPassword());
    }


    @Test
    void setPasswordThrowException() {

        NewPasswordDto passwordDto = new NewPasswordDto();
        passwordDto.setNewPassword("newPassword");
        passwordDto.setCurrentPassword("password");

        SiteUser user = UserConstants.siteUser("username", "password");

        Mockito.when(siteUserRepositoryMock.findByUsername(any(String.class))).thenReturn(user);

        Mockito.when(passwordEncoderMock.matches(any(CharSequence.class), any(String.class))).thenReturn(false);

        Assertions.assertThrows(IncorrectPasswordException.class, () -> out.setPassword(passwordDto, "username"));
    }

    @Test
    void getUserThrowNotFoundException() {

        String email = "some_email@mail.ru";

        Authority authority = new Authority();
        authority.setUsername(email);
        authority.setAuthority("ROLE_ADMIN");

        Mockito.when(authorityRepositoryMock.findAuthorityByUsername(email)).thenReturn(authority);
        Mockito.when(detailsRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> out.getUser(any(Integer.class), email));
    }

    @Test
    void getUserThrowNotAccessException() {

        String email = "some_email@mail.ru";
        Integer id = 1;

        SiteUser user = UserConstants.siteUser("wrong_email@mail.ru", "password");
        SiteUserDetails userDetails = UserConstants.userDetails(id, "Juan", "Carlos", "75-65-98");
        userDetails.setSiteUser(user);

        Authority authority = new Authority();
        authority.setUsername(email);
        authority.setAuthority("ROLE_USER");

        UserDto userDto = UserConstants.createUserDto(id, "wrong_email@mail.ru", "Juan", "Carlos", "75-65-98");

        Mockito.when(authorityRepositoryMock.findAuthorityByUsername(email)).thenReturn(authority);
        Mockito.when(detailsRepositoryMock.findById(id)).thenReturn(Optional.of(userDetails));
        Mockito.when(userMapperMock.fromSiteUserToUserDto(userDetails)).thenReturn(userDto);

        Assertions.assertThrows(NotAccessException.class, () -> out.getUser(id, email));
    }

    @Test
    void getUserByAdmin() {

        String email = "some_email@mail.ru";
        Integer id = 1;

        SiteUser user = UserConstants.siteUser("wrong_email@mail.ru", "password");
        SiteUserDetails userDetails = UserConstants.userDetails(id, "Juan", "Carlos", "75-65-98");
        userDetails.setSiteUser(user);

        Authority authority = new Authority();
        authority.setUsername(email);
        authority.setAuthority("ROLE_ADMIN");

        UserDto userDto = UserConstants.createUserDto(id, "wrong_email@mail.ru", "Juan", "Carlos", "75-65-98");

        Mockito.when(authorityRepositoryMock.findAuthorityByUsername(email)).thenReturn(authority);
        Mockito.when(detailsRepositoryMock.findById(id)).thenReturn(Optional.of(userDetails));
        Mockito.when(userMapperMock.fromSiteUserToUserDto(userDetails)).thenReturn(userDto);

        UserDto result = out.getUser(id, email);

        assertEquals(result.getFirstName(), "Juan");
        assertEquals(result.getLastName(), "Carlos");

    }

    @Test
    void getUserHimSelf() {
        Integer id = 1;
        String email = "some_email@mail.ru";
        String password = "password";
        String name = "Juan";
        String surname = "Carlos";
        String phone = "75-65-98";

        SiteUser user = UserConstants.siteUser(email, password);
        SiteUserDetails userDetails = UserConstants.userDetails(id, name, surname, phone);
        userDetails.setSiteUser(user);

        Authority authority = new Authority();
        authority.setUsername(email);
        authority.setAuthority("ROLE_USER");

        UserDto userDto = UserConstants.createUserDto(id, email, name, surname, phone);

        Mockito.when(authorityRepositoryMock.findAuthorityByUsername(email)).thenReturn(authority);
        Mockito.when(detailsRepositoryMock.findById(id)).thenReturn(Optional.of(userDetails));
        Mockito.when(userMapperMock.fromSiteUserToUserDto(userDetails)).thenReturn(userDto);

        UserDto result = out.getUser(id, email);

        assertEquals(result.getId(), id);
        assertEquals(result.getFirstName(), name);
        assertEquals(result.getLastName(), surname);
        assertEquals(result.getPhone(), phone);
    }




}