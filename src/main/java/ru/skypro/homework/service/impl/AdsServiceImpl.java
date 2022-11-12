package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.exceptionsHandler.exceptions.AdsNotFoundException;
import ru.skypro.homework.exceptionsHandler.exceptions.EmptyListException;
import ru.skypro.homework.exceptionsHandler.exceptions.NotAccessActionException;
import ru.skypro.homework.exceptionsHandler.exceptions.NotAccessException;
import ru.skypro.homework.mappers.impl.AdsMapperImpl;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.repositories.AuthorityRepository;
import ru.skypro.homework.repositories.SiteUserRepository;
import ru.skypro.homework.service.AdsService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class implements methods for working with ads
 */
@Service
public class AdsServiceImpl implements AdsService {

    Logger logger = LoggerFactory.getLogger(AdsServiceImpl.class);

    private final AdsRepository adsRepository;
    private final SiteUserRepository siteUserRepository;
    private final AdsMapperImpl adsMapper;
    private final AuthorityRepository authorityRepository;

    public AdsServiceImpl(AdsRepository adsRepository, SiteUserRepository siteUserRepository,
                          AdsMapperImpl adsMapper, AuthorityRepository authorityRepository) {
        this.adsRepository = adsRepository;
        this.siteUserRepository = siteUserRepository;
        this.adsMapper = adsMapper;
        this.authorityRepository = authorityRepository;
    }

//    method for getting all ads from database
    @Override
    public ResponseWrapper<AdsDto> getAllAds() {
        logger.info("Request for getting all ads from data base");
        List<Ads> adsList = adsRepository.findAll();
        if (adsList.isEmpty()) {
            throw new EmptyListException();
        }
        List<AdsDto> adsDtoList = adsList.stream()
                .map(s -> adsMapper.adsToAdsDto(s))
                .collect(Collectors.toList());
        ResponseWrapper<AdsDto> responseWrapperDto = new ResponseWrapper<>();
        responseWrapperDto.setResults(adsDtoList);
        responseWrapperDto.setCount(adsDtoList.size());
        return responseWrapperDto;
    }

//    Method for getting all ads of one authorized user
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
            if (list.isEmpty()) {
                throw new EmptyListException();
            } else {
                ResponseWrapper<AdsDto> responseWrapperDto = new ResponseWrapper<>();
                responseWrapperDto.setResults(list);
                responseWrapperDto.setCount(list.size());
                return responseWrapperDto;
            }
        } else {
            throw new NotAccessException();
        }
    }

//    method for creating ads
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

//    Method remove ad by id
    @Override
    public String removeAds(Integer idAds) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = authorityRepository.findAuthorityByUsername(email).getAuthority();
        logger.info("Request from user with username: \"{}\" for deleting ad with id {}", email, idAds);
        Optional<Ads> ads = adsRepository.findById(idAds);
        if (ads.isEmpty()) {
            throw new AdsNotFoundException();
        } else {
            Ads deletedAds = ads.get();
            if (role.equals("ROLE_USER") && !deletedAds.getSiteUserDetails().getSiteUser().getUsername().equals(email)) {
                throw new NotAccessActionException();
            } else {
                adsRepository.delete(deletedAds);
                return "SUCCESS";
            }
        }
    }

//     Method for getting full info about ad by id
    @Override
    public FullAds getAds(Integer idAds) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = authorityRepository.findAuthorityByUsername(email).getAuthority();
        logger.info("Request from user with username: \"{}\" for getting full info about ads with id {}", email, idAds);
        Optional<Ads> adsOptional = adsRepository.findById(idAds);
        if (adsOptional.isEmpty()) {
            throw new AdsNotFoundException();
        } else {
            Ads result = adsOptional.get();
            if (role.equals("ROLE_USER") && !result.getSiteUserDetails().getSiteUser().getUsername().equals(email)) {
                throw new NotAccessException();
            } else {
                return adsMapper.adsToFullAds(result, result.getSiteUserDetails());
            }
        }
    }

//    Method for updating ad
    @Override
    public AdsDto updateAds(Integer idAds, AdsDto adsDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = authorityRepository.findAuthorityByUsername(username).getAuthority();
        logger.info("Request for updating ad with id {} from user with username: \"{}\"", idAds, username);
        Optional<Ads> optionalAds = adsRepository.findById(idAds);
        if (optionalAds.isEmpty()) {
            throw new AdsNotFoundException();
        } else {
            if (role.equals("ROLE_USER") && !optionalAds.get().getSiteUserDetails().getSiteUser().getUsername().equals(username)) {
                throw new NotAccessActionException();
            } else {
                Ads result = adsMapper.adsDtoToAds(adsDto, optionalAds.get());
                return adsMapper.adsToAdsDto(adsRepository.save(result));
            }
        }
    }

//    Method for getting ads with title contained text
    @Override
    public ResponseWrapper<AdsDto> getAdsWithTitleContainsText(String text) {
        List<Ads> adsList = adsRepository.findAdsByTitleContains(text);
        if (adsList.isEmpty()) {
            throw new EmptyListException();
        } else {
            List<AdsDto> list = adsList.stream().map(adsMapper::adsToAdsDto).collect(Collectors.toList());
            ResponseWrapper<AdsDto> result = new ResponseWrapper<>();
            result.setResults(list);
            result.setCount(list.size());
            return result;
        }
    }


}
