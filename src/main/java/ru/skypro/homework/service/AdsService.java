package ru.skypro.homework.service;

import ru.skypro.homework.dto.AdsAndUserDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.SiteUser;

public interface AdsService {

    public ResponseWrapper<AdsDto> getAllAds();

    public ResponseWrapper<AdsDto> getAdsMe(Integer price, String title, SiteUser user);

    public CreateAdsDto addAds(CreateAdsDto createAdsDto, Integer id);

    public String removeAds(Integer idAds);

    public AdsAndUserDto getAds(Integer idAds);

    public AdsDto updateAds(Integer idAds, AdsDto ads);
}
