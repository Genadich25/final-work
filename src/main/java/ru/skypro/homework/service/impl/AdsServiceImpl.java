package ru.skypro.homework.service.impl;

import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.service.AdsService;

public class AdsServiceImpl implements AdsService {

    @Override
    public ResponseWrapper<Ads> getAllAds() {
        return null;
    }

    @Override
    public CreateAds addAds(CreateAds createAds) {
        return null;
    }

    @Override
    public ResponseWrapper<Ads> getAdsMe(boolean authenticated, String authority, Object credentials, Object details, Object principal) {
        return null;
    }

    @Override
    public void removeAds(Integer idAds) {
    }

    @Override
    public FullAds getAds(Integer idAds) {
        return null;
    }

    @Override
    public Ads updateAds(Integer idAds) {
        return null;
    }
}
