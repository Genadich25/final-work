package ru.skypro.homework.service;

import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.ResponseWrapper;

public interface AdsService {

    public ResponseWrapper<Ads> getAllAds();

    public CreateAds addAds(CreateAds createAds);

    public ResponseWrapper<Ads> getAdsMe(boolean authenticated,
                                         String authority,
                                         Object credentials,
                                         Object details,
                                         Object principal);

    public void removeAds(Integer idAds);

    public FullAds getAds(Integer idAds);

    public Ads updateAds(Integer idAds);
}
