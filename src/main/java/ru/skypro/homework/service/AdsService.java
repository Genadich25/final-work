package ru.skypro.homework.service;

import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.ResponseWrapper;

/**
 * Interface including methods for working with entity ads
 */
public interface AdsService {


    /**
     * method for getting all ads from database
     * @return ResponseWrapper<AdsDto></AdsDto>
     */
    ResponseWrapper<AdsDto> getAllAds();


    /**
     * Method for getting all ads of one user
     * @return ResponseWrapperDto<AdsDto></AdsDto>
     */
    ResponseWrapper<AdsDto> getAdsMe(Integer price, String title);

    /**
     * method for creating ads
     * @return AdsDto
     */
    AdsDto addAds(CreateAdsDto createAdsDto, String email);

    /**
     * Method remove ad by id
     * @return String
     */
    String removeAds(Integer idAds);

    /**
     * Method for getting full info about ad by id
     * @return FullAds
     */
    FullAds getAds(Integer idAds);

    /**
     * Method for updating ad
     * @return AdsDto
     */
    AdsDto updateAds(Integer idAds, AdsDto ads);

    /**
     * Method for getting ads with title contained text
     * @return ResponseWrapperDto
     */
    ResponseWrapper<AdsDto> getAdsWithTitleContainsText(String text);
}
