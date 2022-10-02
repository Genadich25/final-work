package ru.skypro.homework.service;

import ru.skypro.homework.dto.*;

public interface AdsService {

    public ResponseWrapper<AdsDto> getAllAds();

    public ResponseWrapper<AdsDto> getAdsMe(Integer price, String title, User user);

    public CreateAdsDto addAds(CreateAdsDto createAdsDto, Integer id);

    public String removeAds(Integer idAds);

    public AdsAndUserDto getAds(Integer idAds);

    public AdsDto updateAds(Integer idAds, AdsDto ads);
}
