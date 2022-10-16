package ru.skypro.homework.mappers.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.SiteUserDetails;
import ru.skypro.homework.mappers.AdsMapper;

@Service
public class AdsMapperImpl implements AdsMapper {
    @Override
    public AdsDto adsToAdsDto(Ads ads) {
        AdsDto adsDto = new AdsDto();
        adsDto.setPk(ads.getPk());
        adsDto.setAuthor(ads.getAuthor());
        adsDto.setImage(ads.getImage());
        adsDto.setTitle(ads.getTitle());
        adsDto.setPrice(ads.getPrice());
        return adsDto;
    }

    @Override
    public Ads adsDtoToAds(AdsDto adsDto) {
        Ads ads = new Ads();
        ads.setPk(adsDto.getPk());
        ads.setAuthor(adsDto.getAuthor());
        ads.setImage(adsDto.getImage());
        ads.setTitle(adsDto.getTitle());
        ads.setPrice(adsDto.getPrice());
        return ads;
    }

    @Override
    public Ads adsToCreateAdsDto(CreateAdsDto createAdsDto) {
        Ads ads = new Ads();
        ads.setPk(createAdsDto.getPk());
        ads.setImage(createAdsDto.getImage());
        ads.setTitle(createAdsDto.getTitle());
        ads.setPrice(createAdsDto.getPrice());
        ads.setDescription(createAdsDto.getDescription());
        return ads;
    }

    @Override
    public CreateAdsDto createAdsDtoToAds(Ads ads) {
        CreateAdsDto createAdsDto = new CreateAdsDto();
        createAdsDto.setPk(ads.getPk());
        createAdsDto.setImage(ads.getImage());
        createAdsDto.setTitle(ads.getTitle());
        createAdsDto.setPrice(ads.getPrice());
        createAdsDto.setDescription(ads.getDescription());
        return createAdsDto;
    }

    @Override
    public FullAds adsToFullAds(Ads ads, SiteUserDetails user) {
        FullAds fullAds = new FullAds();
        fullAds.setImage(ads.getImage());
        fullAds.setTitle(ads.getTitle());
        fullAds.setPrice(ads.getPrice());
        fullAds.setDescription(ads.getDescription());
        fullAds.setPk(ads.getPk());
        fullAds.setAuthorFirstName(user.getFirstName());
        fullAds.setAuthorLastName(user.getLastName());
        fullAds.setEmail(user.getSiteUser().getUsername());
        fullAds.setPhone(user.getPhone());
        return fullAds;
    }


}
