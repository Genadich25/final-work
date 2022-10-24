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
        adsDtoResponseWrapper.setResults(result);
        return adsDtoResponseWrapper;
    }

    @Override
    public ResponseWrapper<AdsDto> getAdsMe(Integer price, String title) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Request for getting all ads of user with username {}", email);
        Optional<SiteUser> optionalSiteUser = siteUserRepository.findSiteUserByUsername(email);
        if (optionalSiteUser.isPresent()) {
            Integer idUser = optionalSiteUser.get().getSiteUserDetails().getId();
            List<AdsDto> list = adsRepository.findAdsBySiteUserDetailsId(idUser)
                    .stream()
                    .map(adsMapper::adsToAdsDto)
                    .collect(Collectors.toList());
            if (price != null && title == null) {
                list = adsRepository.findAllBySiteUserDetailsIdAndPrice(idUser, price)
                        .stream()
                        .map(adsMapper::adsToAdsDto)
                        .collect(Collectors.toList());
            }
            if (title != null && price == null) {
                list = adsRepository.findAllBySiteUserDetailsIdAndTitleContains(idUser, title)
                        .stream()
                        .map(adsMapper::adsToAdsDto)
                        .collect(Collectors.toList());
            }
            if (title != null && price != null) {
                list = adsRepository.findAllBySiteUserDetailsIdAndPriceAndTitleContains(idUser, price, title)
                        .stream()
                        .map(adsMapper::adsToAdsDto)
                        .collect(Collectors.toList());
            }
            ResponseWrapper<AdsDto> responseWrapperDto = new ResponseWrapper<>();
            responseWrapperDto.setResults(list);
            responseWrapperDto.setCount(list.size());
            return responseWrapperDto;
        } else {
            return null;
        }

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
        Optional<Ads> ads = adsRepository.findById(idAds);
        if (ads.isEmpty()) {
            return null;
        } else {
            if (auth.equals("ROLE_USER") && !ads.get().getSiteUserDetails().getSiteUser().getUsername().equals(username)) {
                return "Not access";
            } else {
                adsRepository.delete(ads.get());
                return "Обявление" + ads + "удалено";
            }
        }
    }

    @Override
    public FullAds getAds(Integer idAds) {
        logger.info("Получение полной информации об объявлении по id");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String auth = authorityRepository.findAuthorityByUsername(username).getAuthority();
        Optional<Ads> ads = adsRepository.findById(idAds);
        if (ads.isEmpty()) {
            return null;
        } else {
            if (auth.equals("ROLE_USER") && !ads.get().getSiteUserDetails().getSiteUser().getUsername().equals(username)) {
                FullAds result = new FullAds();
                result.setTitle("Not access");
                return result;
            } else {
                SiteUserDetails userDetails = userRepository.getReferenceById(ads.get().getAuthor());
                return adsMapper.adsToFullAds(ads.get(), userDetails);
            }
        }
    }

    @Override
    public AdsDto updateAds(Integer idAds, AdsDto adsDto) {
        logger.info("Изменение объявления по id");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String auth = authorityRepository.findAuthorityByUsername(username).getAuthority();
        Optional<Ads> adsOld = adsRepository.findById(idAds);
        if (adsOld.isEmpty()) {
            return null;
        } else {
            if (auth.equals("ROLE_USER") && !adsOld.get().getSiteUserDetails().getSiteUser().getUsername().equals(username)) {
                AdsDto result = new AdsDto();
                result.setTitle("Not access");
                return result;
            } else {
                Ads adsNew = adsMapper.adsDtoToAds(adsDto, adsOld.get());
                adsRepository.save(adsNew);
                return adsDto;
            }
        }
    }

    @Override
    public ResponseWrapper<AdsDto> getAdsWithTitleContainsText(String text) {
        List<Ads> adsList = adsRepository.findAdsByTitleContains(text);
        if (adsList.isEmpty()) {
            return null;
        } else {
            List<AdsDto> list = adsList.stream().map(adsMapper::adsToAdsDto).collect(Collectors.toList());
            ResponseWrapper<AdsDto> result = new ResponseWrapper<>();
            result.setResults(list);
            result.setCount(list.size());
            return result;
        }
    }


}
