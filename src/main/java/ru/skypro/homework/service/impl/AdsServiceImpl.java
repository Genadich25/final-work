package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.mapper.CreateAdsMapper;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.service.AdsService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdsServiceImpl implements AdsService {

    Logger logger = LoggerFactory.getLogger(AdsServiceImpl.class);
    @Autowired
    private AdsRepository adsRepository;
    @Autowired
    private UserServiceImpl userService;

    private UserDetails userDetails;

    @Override
    public ResponseWrapper<AdsDto> getAllAds() {
        logger.info("Получаем список всех объявлений");
        List<Ads> allAds = adsRepository.findAll();
        List<AdsDto> result = allAds.stream()
                .map(s -> AdsMapper.INSTANCE.adsToAdsDto(s))
                .collect(Collectors.toList());
        ResponseWrapper<AdsDto> adsDtoResponseWrapper = new ResponseWrapper<>();
        adsDtoResponseWrapper.setCount(result.size());
        adsDtoResponseWrapper.setList(result);
        return adsDtoResponseWrapper;
    }

    @Override
    public ResponseWrapper<AdsDto> getAdsMe() {

        return null;
    }

    @Override
    public CreateAdsDto addAds(CreateAdsDto createAdsDto, Integer id) {
        logger.info("Создание нового объвления");
        Ads ads = CreateAdsMapper.INSTANCE.adsToAdsDto(createAdsDto);
        ads.setAuthor(id);
        adsRepository.save(ads);
        return createAdsDto;
    }

    @Override
    public Ads removeAds(Integer idAds) {
        return null;
    }

    @Override
    public Ads getAds(Integer idAds) {
        return null;
    }

    @Override
    public Ads updateAds(Integer idAds) {
        return null;
    }
}
