package ru.skypro.homework.mappers;

import org.mapstruct.Mapping;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.SiteUserDetails;


public interface AdsMapper {

    AdsDto adsToAdsDto(Ads ads);

    Ads adsDtoToAds(AdsDto adsDto);

    Ads adsToCreateAdsDto(CreateAdsDto ads);

    CreateAdsDto createAdsDtoToAds(Ads ads);

    FullAds adsToFullAds(Ads ads, SiteUserDetails user);

}