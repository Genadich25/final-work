package ru.skypro.homework.service;

import ru.skypro.homework.dto.Ads;

import java.util.List;

public interface AdsService {

    public List<Ads> getAllAds();

    public Ads getAdsMe();

    public Ads addAds(Ads ads);

    public Ads removeAds(Integer idAds);

    public Ads getAds(Integer idAds);

    public Ads updateAds(Integer idAds);
}
