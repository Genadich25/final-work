package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.mapper.CreateAdsMapper;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.service.AdsService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdsServiceImpl implements AdsService {

    Logger logger = LoggerFactory.getLogger(AdsServiceImpl.class);
    @Autowired
    private AdsRepository adsRepository;

    @Override
    public ResponseWrapper<AdsDto> getAllAds() {
        logger.info("Получаем список всех объявлений");
        List<Ads> allAds = adsRepository.findAll();
        List<AdsDto> result = allAds.stream()
                .map(AdsMapper.INSTANCE::adsToAdsDto)
                .collect(Collectors.toList());
        ResponseWrapper<AdsDto> adsDtoResponseWrapper = new ResponseWrapper<>();
        adsDtoResponseWrapper.setCount(result.size());
        adsDtoResponseWrapper.setList(result);
        return adsDtoResponseWrapper;
    }

    @Override
    public ResponseWrapper<AdsDto> getAdsMe(Integer price, String title, User user) {
        logger.info("Получние объявлений пренадлежащих пользователю");
        List<Ads> adsMe = adsRepository.findByAuthorAndPriceAndTitle(user.getId(), price, title);
        List<AdsDto> result = adsMe.stream()
                .map(AdsMapper.INSTANCE::adsToAdsDto)
                .collect(Collectors.toList());
        ResponseWrapper<AdsDto> adsDtoResponseWrapper = new ResponseWrapper<>();
        adsDtoResponseWrapper.setCount(result.size());
        adsDtoResponseWrapper.setList(result);
        return adsDtoResponseWrapper;
    }

    @Override
    public CreateAdsDto addAds(CreateAdsDto createAdsDto, Integer id) {
        logger.info("Создание нового объвления");
        Ads ads = AdsMapper.INSTANCE.adsToCreateAdsDto(createAdsDto);
        ads.setAuthor(id);
        adsRepository.save(ads);
        return createAdsDto;
    }

    @Override
    public String removeAds(Integer idAds) {
        logger.info("Удаление объявления по id");
        Ads ads = adsRepository.findAdsById(idAds);
        adsRepository.delete(ads);
        return "Обявление" + ads + "удалено";
    }

    @Override
    public AdsAndUserDto getAds(Integer idAds) {
        logger.info("Получение объявления по id");
        Ads ads = adsRepository.findAdsById(idAds);
        CreateAdsDto createAdsDto= CreateAdsMapper.INSTANCE.adsToAdsDto(ads);
        CreateUser createUser = new CreateUser();
        AdsAndUserDto adsAndUserDto= AdsMapper.INSTANCE.createAdsAndUserDto(createAdsDto, createUser);
        return adsAndUserDto;
    }

    @Override
    public AdsDto updateAds(Integer idAds, AdsDto adsDto) {
        logger.info("Изменение объявления по id");
        Ads adsOld = adsRepository.findAdsById(idAds);
        Ads adsNew = AdsMapper.INSTANCE.adsDtoToAds(adsDto);
        adsRepository.save(adsNew);
        return adsDto;
    }
}
