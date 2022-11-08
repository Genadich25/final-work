package ru.skypro.homework.constants;

import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.entities.SiteUserDetails;

public class UserConstants {

    public static UserDto createUserDto(Integer id, String email, String firstName, String lastName, String phone) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        return user;
    }

    public static RegisterReq createReq(String username, String password, String firstName, String lastName, String phone, Role role) {
        RegisterReq req = new RegisterReq();
        req.setUsername(username);
        req.setPassword(password);
        req.setFirstName(firstName);
        req.setLastName(lastName);
        req.setPhone(phone);
        req.setRole(role);
        return req;
    }

    public static SiteUserDetails userDetails(Integer id, String firstName, String lastName, String phone) {
        SiteUserDetails user = new SiteUserDetails();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        return user;
    }

    public static SiteUser siteUser(String userName, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(userName);
        user.setPassword(password);
        return user;
    }

}
