package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.entities.SiteUserDetails;
import ru.skypro.homework.mappers.impl.AdsMapperImpl;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.repositories.AuthorityRepository;
import ru.skypro.homework.repositories.SiteUserRepository;
import ru.skypro.homework.repositories.UserDetailsRepository;
import ru.skypro.homework.service.AdsService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdsServiceImpl implements AdsService {

    Logger logger = LoggerFactory.getLogger(AdsServiceImpl.class);

    private final AdsRepository adsRepository;
    private final UserDetailsRepository userRepository;
    private final SiteUserRepository siteUserRepository;
    private final AdsMapperImpl adsMapper;
    private final AuthorityRepository authorityRepository;

    public AdsServiceImpl(AdsRepository adsRepository, UserDetailsRepository userRepository, SiteUserRepository siteUserRepository,
                          AdsMapperImpl adsMapper, AuthorityRepository authorityRepository) {
        this.adsRepository = adsRepository;
        this.userRepository = userRepository;
        this.siteUserRepository = siteUserRepository;
        this.adsMapper = adsMapper;
        this.authorityRepository = authorityRepository;
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
    public AdsDto addAds(CreateAdsDto createAdsDto, String email) {
        logger.info("Create new ad by user with username: {}", email);
        Optional<SiteUser> siteUser = siteUserRepository.findSiteUserByUsername(email);
        if (siteUser.isEmpty()) {
            return null;
        } else {
            Ads ads = adsMapper.adsToCreateAdsDto(createAdsDto);
            ads.setAuthor(siteUser.get().getSiteUserDetails().getId());
            ads.setSiteUserDetails(siteUser.get().getSiteUserDetails());
            return adsMapper.adsToAdsDto(adsRepository.save(ads));
        }
    }

    @Override
    public String removeAds(Integer idAds) {
        logger.info("Удаление объявления по id");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String auth = authorityRepository.findAuthorityByUsername(username).getAuthority();
        Ads ads = adsRepository.findAdsByPk(idAds);
        if (auth.equals("ROLE_USER") && !ads.getSiteUserDetails().getSiteUser().getUsername().equals(username)) {
            return "Not access";
        } else {
            adsRepository.delete(ads);
            return "Обявление" + ads + "удалено";
        }
    }

    @Override
    public FullAds getAds(Integer idAds) {
        logger.info("Получение объявления по id");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String auth = authorityRepository.findAuthorityByUsername(username).getAuthority();
        Ads ads = adsRepository.findAdsByPk(idAds);
        if (auth.equals("ROLE_USER") && !ads.getSiteUserDetails().getSiteUser().getUsername().equals(username)) {
            FullAds result = new FullAds();
            result.setTitle("Not access");
            return result;
        }
        SiteUserDetails userDetails = userRepository.getReferenceById(ads.getAuthor());
        return adsMapper.adsToFullAds(ads, userDetails);
    }

    @Override
    public AdsDto updateAds(Integer idAds, AdsDto adsDto) {
        logger.info("Изменение объявления по id");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String auth = authorityRepository.findAuthorityByUsername(username).getAuthority();
        Ads adsOld = adsRepository.findAdsByPk(idAds);
        if (auth.equals("ROLE_USER") && !adsOld.getSiteUserDetails().getSiteUser().getUsername().equals(username)) {
            AdsDto result = new AdsDto();
            result.setTitle("Not access");
            return result;
        }
        Ads adsNew = adsMapper.adsDtoToAds(adsDto, adsOld);
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
