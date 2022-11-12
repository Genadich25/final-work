package ru.skypro.homework.mappers.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.SiteUserDetails;
import ru.skypro.homework.mappers.AdsMapper;

/**
 * Class implements methods for converting entity ads to dto and back again
 */
@Service
public class AdsMapperImpl implements AdsMapper {

//    method converts from ads entity to ads dto
    @Override
    public AdsDto adsToAdsDto(Ads ads) {
        AdsDto adsDto = new AdsDto();
        if (ads.getId() != null) {
            adsDto.setPk(ads.getId());
        }
        if (ads.getAuthor() != null) {
            adsDto.setAuthor(ads.getAuthor());
        }
        if (ads.getImage() != null) {
            adsDto.setImage(ads.getImage());
        }
        if (ads.getTitle() != null) {
            adsDto.setTitle(ads.getTitle());
        }
        if (ads.getPrice() != null) {
            adsDto.setPrice(ads.getPrice());
        }
        return adsDto;
    }

//     method converts from ads dto to ads entity
    @Override
    public Ads adsDtoToAds(AdsDto adsDto, Ads ads) {
        if (adsDto.getTitle() != null) {
            ads.setTitle(adsDto.getTitle());
        }
        if (adsDto.getPrice() != null) {
            ads.setPrice(adsDto.getPrice());
        }
        return ads;
    }

//     method converts from create ads dto to ads entity
    @Override
    public Ads adsToCreateAdsDto(CreateAdsDto createAdsDto) {
        Ads ads = new Ads();
        if (createAdsDto.getImage() != null) {
            ads.setImage(createAdsDto.getImage());
        }
        ads.setTitle(createAdsDto.getTitle());
        ads.setPrice(createAdsDto.getPrice());
        ads.setDescription(createAdsDto.getDescription());
        return ads;
    }

//    method converts from ads entity and site user details entity to full ads dto
    @Override
    public FullAds adsToFullAds(Ads ads, SiteUserDetails user) {
        FullAds fullAds = new FullAds();
        fullAds.setImage(ads.getImage());
        fullAds.setTitle(ads.getTitle());
        fullAds.setPrice(ads.getPrice());
        fullAds.setDescription(ads.getDescription());
        fullAds.setPk(ads.getId());
        fullAds.setAuthorFirstName(user.getFirstName());
        fullAds.setAuthorLastName(user.getLastName());
        fullAds.setEmail(user.getSiteUser().getUsername());
        fullAds.setPhone(user.getPhone());
        return fullAds;
    }


}
