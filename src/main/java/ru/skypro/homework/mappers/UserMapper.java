package ru.skypro.homework.mappers;

import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.SiteUser;

public interface UserMapper {

    SiteUser fromCreateUserDtoToSiteUser(CreateUserDto userDto);

    CreateUserDto fromSiteUserToCreateUserDto(SiteUser user);

    SiteUser fromUserDtoToSiteUser(SiteUser siteUser, UserDto userDto);

    UserDto fromSiteUserToUserDto(SiteUser user);
}
