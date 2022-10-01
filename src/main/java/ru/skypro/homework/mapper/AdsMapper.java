package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdsDto;
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
}