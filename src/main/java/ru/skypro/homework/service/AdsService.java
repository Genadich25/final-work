package ru.skypro.homework.service;

import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.Ads;

public interface AdsService {

    public ResponseWrapper<AdsDto> getAllAds();

    public ResponseWrapper<AdsDto> getAdsMe();

    public CreateAdsDto addAds(CreateAdsDto createAdsDto, Integer id);

    public Ads removeAds(Integer idAds);

    public Ads getAds(Integer idAds);

    public Ads updateAds(Integer idAds);
}
