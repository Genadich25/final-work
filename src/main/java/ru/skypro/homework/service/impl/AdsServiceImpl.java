package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.entities.SiteUserDetails;
import ru.skypro.homework.mappers.impl.AdsMapperImpl;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.repositories.UserDetailsRepository;
import ru.skypro.homework.service.AdsService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdsServiceImpl implements AdsService {

    Logger logger = LoggerFactory.getLogger(AdsServiceImpl.class);
    private final AdsRepository adsRepository;
    private final UserDetailsRepository userRepository;

    private final AdsMapperImpl adsMapper;

    public AdsServiceImpl(AdsRepository adsRepository, UserDetailsRepository userRepository, AdsMapperImpl adsMapper) {
        this.adsRepository = adsRepository;
        this.userRepository = userRepository;
        this.adsMapper = adsMapper;
    }

    @Override
    public ResponseWrapper<AdsDto> getAllAds() {
        logger.info("Получаем список всех объявлений");
        List<Ads> allAds = adsRepository.findAll();
        List<AdsDto> result = allAds.stream()
                .map(adsMapper::adsToAdsDto)
                .collect(Collectors.toList());
        ResponseWrapper<AdsDto> adsDtoResponseWrapper = new ResponseWrapper<>();
        adsDtoResponseWrapper.setCount(result.size());
        adsDtoResponseWrapper.setList(result);
        return adsDtoResponseWrapper;
    }

    @Override
    public ResponseWrapper<AdsDto> getAdsMe(Integer price, String title, SiteUser user) {
        logger.info("Получние объявлений пренадлежащих пользователю");
        List<Ads> adsMe = adsRepository.findByAuthorAndPriceAndTitle(user.getSiteUserDetails().getId(), price, title);
        List<AdsDto> result = adsMe.stream()
                .map(adsMapper::adsToAdsDto)
                .collect(Collectors.toList());
        ResponseWrapper<AdsDto> adsDtoResponseWrapper = new ResponseWrapper<>();
        adsDtoResponseWrapper.setCount(result.size());
        adsDtoResponseWrapper.setList(result);
        return adsDtoResponseWrapper;
    }

    @Override
    public CreateAdsDto addAds(CreateAdsDto createAdsDto, Integer id) {
        logger.info("Создание нового объвления");
        Ads ads = adsMapper.adsToCreateAdsDto(createAdsDto);
        ads.setAuthor(id);
        adsRepository.save(ads);
        return createAdsDto;
    }

    @Override
    public String removeAds(Integer idAds) {
        logger.info("Удаление объявления по id");
        Ads ads = adsRepository.findAdsByPk(idAds);
        adsRepository.delete(ads);
        return "Обявление" + ads + "удалено";
    }

    @Override
    public FullAds getAds(Integer idAds) {
        logger.info("Получение объявления по id");
        Ads ads = adsRepository.findAdsByPk(idAds);
        SiteUserDetails userDetails = userRepository.getReferenceById(ads.getAuthor());
        FullAds fullAds = adsMapper.adsToFullAds(ads, userDetails);
        return fullAds;
    }

    @Override
    public AdsDto updateAds(Integer idAds, AdsDto adsDto) {
        logger.info("Изменение объявления по id");
        Ads adsOld = adsRepository.findAdsByPk(idAds);
        Ads adsNew = adsMapper.adsDtoToAds(adsDto);
        adsRepository.save(adsNew);
        return adsDto;
    }

    @Override
    public ResponseWrapper<AdsDto> getAdsWithTitleContainsText(String text) {
        List<Ads> adsList = adsRepository.findAdsByTitleContains(text);
        if (adsList.isEmpty()) {
            return null;
        } else {
            List<AdsDto> list = adsList.stream().map(adsMapper::adsToAdsDto).collect(Collectors.toList());
            ResponseWrapper<AdsDto> result = new ResponseWrapper<>();
            result.setList(list);
            result.setCount(list.size());
            return result;
        }
    }


}
