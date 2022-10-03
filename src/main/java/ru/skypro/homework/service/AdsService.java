package ru.skypro.homework.service;

import ru.skypro.homework.dto.*;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.ResponseWrapper;

public interface AdsService {

    public ResponseWrapper<AdsDto> getAllAds();
    public ResponseWrapper<Ads> getAllAds();

    public ResponseWrapper<AdsDto> getAdsMe(Integer price, String title, User user);
    public CreateAds addAds(CreateAds createAds);

    public CreateAdsDto addAds(CreateAdsDto createAdsDto, Integer id);
    public ResponseWrapper<Ads> getAdsMe(boolean authenticated,
                                         String authority,
                                         Object credentials,
                                         Object details,
                                         Object principal);

    public String removeAds(Integer idAds);

    public AdsAndUserDto getAds(Integer idAds);
    public FullAds getAds(Integer idAds);

    public AdsDto updateAds(Integer idAds, AdsDto ads);
}
