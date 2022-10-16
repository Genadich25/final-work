package ru.skypro.homework.mappers;

import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.entities.SiteUserDetails;

public interface UserMapper {

    SiteUser fromUserDtoToSiteUser(SiteUser siteUser, UserDto userDto);

    UserDto fromSiteUserToUserDto(SiteUserDetails user);
}
