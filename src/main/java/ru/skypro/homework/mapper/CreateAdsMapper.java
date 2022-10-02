package ru.skypro.homework.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.entities.Ads;

public interface CreateAdsMapper {

    CreateAdsMapper INSTANCE = Mappers.getMapper(CreateAdsMapper.class);

    @Mapping(source  = "image", target  = "image")
    @Mapping(source  = "pk", target  = "pk")
    @Mapping(source  = "price", target  = "price")
    @Mapping(source  = "title", target  = "title")
    Ads adsToAdsDto(CreateAdsDto ads);
}
