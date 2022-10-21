package ru.skypro.homework.service;

import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.SiteUser;

public interface AdsService {

    ResponseWrapper<AdsDto> getAllAds();

    ResponseWrapper<AdsDto> getAdsMe(Integer price, String title, SiteUser user);

    AdsDto addAds(CreateAdsDto createAdsDto, String email);

    String removeAds(Integer idAds);

    FullAds getAds(Integer idAds);

    AdsDto updateAds(Integer idAds, AdsDto ads);

    ResponseWrapper<AdsDto> getAdsWithTitleContainsText(String text);
}
