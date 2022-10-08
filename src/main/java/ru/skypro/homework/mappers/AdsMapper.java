package ru.skypro.homework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdsAndUserDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.entities.Ads;

@Mapper
public interface AdsMapper {

    AdsMapper INSTANCE = Mappers.getMapper(AdsMapper.class);

    @Mapping(source  = "author", target  = "author")
    @Mapping(source  = "image", target  = "image")
    @Mapping(source  = "pk", target  = "pk")
    @Mapping(source  = "price", target  = "price")
    @Mapping(source  = "title", target  = "title")
    AdsDto adsToAdsDto(Ads ads);

    @Mapping(source  = "author", target  = "author")
    @Mapping(source  = "image", target  = "image")
    @Mapping(source  = "pk", target  = "pk")
    @Mapping(source  = "price", target  = "price")
    @Mapping(source  = "title", target  = "title")
    Ads adsDtoToAds(AdsDto adsDto);

    @Mapping(source  = "image", target  = "image")
    @Mapping(source  = "pk", target  = "pk")
    @Mapping(source  = "price", target  = "price")
    @Mapping(source  = "title", target  = "title")
    Ads adsToCreateAdsDto(CreateAdsDto ads);

    @Mapping(source  = "firstName", target  = "firstName")
    @Mapping(source  = "lastName", target  = "lastName")
    @Mapping(source  = "phone", target  = "phone")
    @Mapping(source  = "email", target  = "email")
    @Mapping(source  = "password", target  = "password")
    @Mapping(source  = "image", target  = "image")
    @Mapping(source  = "pk", target  = "pk")
    @Mapping(source  = "price", target  = "price")
    @Mapping(source  = "title", target  = "title")
    AdsAndUserDto createAdsAndUserDto(CreateAdsDto createAdsDto, CreateUserDto createUser);

}