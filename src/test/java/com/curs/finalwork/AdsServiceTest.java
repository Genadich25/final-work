package com.curs.finalwork;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.webjars.NotFoundException;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.SiteUserDetails;
import ru.skypro.homework.mappers.impl.AdsMapperImpl;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.repositories.AuthorityRepository;
import ru.skypro.homework.repositories.SiteUserRepository;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.impl.AdsServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase
public class AdsServiceTest {

    private Ads ads1;
    private AdsMapperImpl adsMapper = new AdsMapperImpl();
    @Mock
    private AdsRepository adsRepository;
    @Mock
    private SiteUserRepository siteUserRepository;
    @Mock
    private AuthorityRepository authorityRepository;
    @Mock
    private AdsServiceImpl adsServiceImpl = new AdsServiceImpl(adsRepository,siteUserRepository,adsMapper,authorityRepository);

    @InjectMocks
    private AdsService adsService = adsServiceImpl;

    @BeforeEach
    public void setUp() {

        ads1 = new Ads();
        ads1.setAuthor(1);
        ads1.setPrice(1000);
        ads1.setTitle("pen");
        ads1.setDescription("Made in Russia");
    }

    @Test
    public void getAllAdsTest() {

        List<Ads> adsList = new ArrayList<>();
        adsList.add(ads1);

        when(adsRepository.findAll()).thenReturn(adsList);

        List<Ads> ads = adsRepository.findAll();

        ResponseWrapper<AdsDto> adsDtoResponseWrapper = new ResponseWrapper<>();
        List<AdsDto> adsListDto = ads.stream()
                .map(s -> adsMapper.adsToAdsDto(s))
                .collect(Collectors.toList());
        adsDtoResponseWrapper.setResults(adsListDto);
        adsDtoResponseWrapper.setCount(adsListDto.size());

        when(adsServiceImpl.getAllAds()).thenReturn(adsDtoResponseWrapper);

        ResponseWrapper<AdsDto> responseWrapperRes = adsService.getAllAds();

        assertEquals(adsDtoResponseWrapper, responseWrapperRes);
    }

    @Test
    public void addAds() {

        when(adsRepository.save(any(Ads.class))).thenReturn(ads1);
        when(adsRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(ads1));

        AdsDto adsDtoEx = adsMapper.adsToAdsDto(ads1);

        CreateAdsDto createAds = new CreateAdsDto();
        createAds.setDescription(ads1.getDescription());
        createAds.setImage(ads1.getImage());
        createAds.setPrice(ads1.getPrice());
        createAds.setTitle(ads1.getTitle());

        String email = "hdbcihd@hg.ru";
        AdsDto adsDtoRes = adsService.addAds(createAds, email);

        assertEquals(adsDtoEx, adsDtoRes);
    }

    @Test
    public void removeAds() {

        Integer idAds = 1;
        doNothing().when(adsRepository).deleteById(idAds);

        adsRepository.delete(ads1);

        assertThrows(NotFoundException.class, null);
    }

    @Test
    public void getAds() {

        SiteUserDetails siteUserDetails = new SiteUserDetails();
        siteUserDetails.setId(1);
        FullAds fullAdsEx = adsMapper.adsToFullAds(ads1, siteUserDetails);
        when(adsServiceImpl.getAds(any(Integer.class))).thenReturn(fullAdsEx);

        FullAds fullAdsRes = adsService.getAds(1);

        assertEquals(fullAdsEx,fullAdsRes);

    }

    @Test
    public void updateAds() {

        AdsDto adsDtoEx = adsMapper.adsToAdsDto(ads1);
        when(adsServiceImpl.updateAds(any(Integer.class), any(AdsDto.class))).thenReturn(adsDtoEx);

        AdsDto adsDtoRes = adsService.updateAds(1, adsDtoEx);

        assertEquals(adsDtoEx, adsDtoRes);

    }
}
