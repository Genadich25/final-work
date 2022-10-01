package ru.skypro.homework.service;

import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.Ads;

public interface AdsService {

    public ResponseWrapper<AdsDto> getAllAds();

    public Ads getAdsMe();

    public Ads addAds(Ads ads);

    public Ads removeAds(Integer idAds);

    public Ads getAds(Integer idAds);

    public Ads updateAds(Integer idAds);
}
