package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.service.AdsService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdsServiceImpl implements AdsService {

    @Autowired
    AdsRepository adsRepository;

    @Override
    public ResponseWrapper<AdsDto> getAllAds() {
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
    public Ads getAdsMe() {
        return null;
    }

    @Override
    public Ads addAds(Ads ads) {
        return null;
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
