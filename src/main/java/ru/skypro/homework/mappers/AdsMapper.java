package ru.skypro.homework.mappers;

import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.SiteUserDetails;

/**
 * Interface including methods for converting entity ads to ads dto (or full ads dto) or
 * converting create ads dto to ads entity
 */
public interface AdsMapper {

    AdsDto adsToAdsDto(Ads ads);

    Ads adsDtoToAds(AdsDto adsDto, Ads ads);

    Ads adsToCreateAdsDto(CreateAdsDto ads);

    FullAds adsToFullAds(Ads ads, SiteUserDetails user);

}