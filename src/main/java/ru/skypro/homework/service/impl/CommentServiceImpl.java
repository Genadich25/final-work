package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.Comment;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.exceptionsHandler.exceptions.*;
import ru.skypro.homework.mappers.impl.CommentMapperImpl;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.repositories.AuthorityRepository;
import ru.skypro.homework.repositories.CommentRepository;
import ru.skypro.homework.repositories.SiteUserRepository;
import ru.skypro.homework.service.CommentService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class implements methods for working with comments
 */
@Service
public class CommentServiceImpl implements CommentService {

    Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;
    private final AdsRepository adsRepository;
    private final CommentMapperImpl commentMapper;
    private final AuthorityRepository authorityRepository;
    private final SiteUserRepository siteUserRepository;

    public CommentServiceImpl(CommentRepository commentRepository, AdsRepository adsRepository, CommentMapperImpl commentMapper,
                              AuthorityRepository authorityRepository, SiteUserRepository siteUserRepository) {
        this.commentRepository = commentRepository;
        this.adsRepository = adsRepository;
        this.commentMapper = commentMapper;
        this.authorityRepository = authorityRepository;
        this.siteUserRepository = siteUserRepository;
    }

    //    Method for getting all comments of one ad
    @Override
    public ResponseWrapper<CommentDto> getListCommentDto(String idAds) {
        logger.info("Request for getting all comments of ad with id {}", idAds);
        Optional<Ads> optionalAds = adsRepository.findById(Integer.parseInt(idAds));
        if (optionalAds.isEmpty()) {
            throw new IncorrectAdsIdException();
        } else {
            List<Comment> commentList = commentRepository.findCommentsByAds_Id(Integer.parseInt(idAds));
            if (commentList.isEmpty()) {
                throw new EmptyListException();
            } else {
                List<CommentDto> commentDtoList = commentList.stream()
                        .map(commentMapper::commentToCommentDto)
                        .collect(Collectors.toList());
                ResponseWrapper<CommentDto> commentDtoResponseWrapper = new ResponseWrapper<>();
                commentDtoResponseWrapper.setResults(commentDtoList);
                commentDtoResponseWrapper.setCount(commentDtoList.size());
                return commentDtoResponseWrapper;
            }
        }
    }

    //    Method for creating comment for one ad
    @Override
    public CommentDto addCommentDto(String idAds, String text) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Request for adding comment with text: \"{}\" to ad with id {} from user with username: {}", text, idAds, username);
        Optional<Ads> adsOptional = adsRepository.findById(Integer.parseInt(idAds));
        if (adsOptional.isEmpty()) {
            throw new AdsNotFoundException();
        } else {
            SiteUser siteUser = siteUserRepository.findByUsername(username);
            Comment result = new Comment();
            result.setSiteUserDetails(siteUser.getSiteUserDetails());
            result.setAds(adsOptional.get());
            result.setIdAuthor(siteUser.getSiteUserDetails().getId());
            result.setText(text);
            result.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            return commentMapper.commentToCommentDto(commentRepository.save(result));
        }
    }

    //     Method remove one comment of ad by ad id and comment id
    @Override
    public String deleteCommentDto(String idAds, Integer idComment) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = authorityRepository.findAuthorityByUsername(email).getAuthority();
        logger.info("Request from user with username: \"{}\" for deleting comment with id: {}", email, idComment);
        Optional<Ads> adsOptional = adsRepository.findById(Integer.parseInt(idAds));
        if (adsOptional.isEmpty()) {
            throw new IncorrectAdsIdException();
        } else {
            List<Comment> commentList = commentRepository.findCommentsByAds_Id(Integer.parseInt(idAds));
            Optional<Comment> optionalAdsComment = commentRepository.findById(idComment);
            if (optionalAdsComment.isEmpty()) {
                throw new CommentNotFoundException();
            } else {
                if (commentList.isEmpty() || !commentList.contains(optionalAdsComment.get())) {
                    throw new CommentNotBelongAdException();
                } else {
                    if (role.equals("ROLE_USER") && !optionalAdsComment.get().getSiteUserDetails().getSiteUser().getUsername().equals(email)) {
                        throw new NotAccessActionException();
                    } else {
                        commentRepository.delete(optionalAdsComment.get());
                        return "SUCCESS";
                    }
                }
            }
        }
    }

    //    Method for getting one comment of ad by ad id and comment id
    @Override
    public CommentDto getCommentDto(String idAds, Integer idComment) {
        logger.info("Request for getting information about comment with id: {} of ads with id: {}", idComment, idAds);
        Optional<Ads> optionalAds = adsRepository.findById(Integer.parseInt(idAds));
        if (optionalAds.isEmpty()) {
            throw new IncorrectAdsIdException();
        } else {
            List<Comment> commentList = commentRepository.findCommentsByAds_Id(Integer.parseInt(idAds));
            Optional<Comment> optionalAdsComment = commentRepository.findById(idComment);
            if (optionalAdsComment.isEmpty()) {
                throw new CommentNotFoundException();
            } else {
                if (commentList.isEmpty() || !commentList.contains(optionalAdsComment.get())) {
                    throw new CommentNotBelongAdException();
                } else {
                    return commentMapper.commentToCommentDto(optionalAdsComment.get());
                }
            }
        }
    }

//    Method for updating comment
    @Override
    public CommentDto updateCommentDto(String idAds, Integer idComment, CommentDto commentDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String auth = authorityRepository.findAuthorityByUsername(username).getAuthority();
        logger.info("Request from user with username: \"{}\" for updating comment with id {}", username, idComment);
        Optional<Ads> ads = adsRepository.findById(Integer.parseInt(idAds));
        if (ads.isEmpty()) {
            throw new IncorrectAdsIdException();
        } else {
            List<Comment> commentList = commentRepository.findCommentsByAds_Id(Integer.parseInt(idAds));
            Optional<Comment> optionalAdsComment = commentRepository.findById(idComment);
            if (optionalAdsComment.isEmpty()) {
                throw new CommentNotFoundException();
            } else {
                if (commentList.isEmpty() || !commentList.contains(optionalAdsComment.get())) {
                    throw new CommentNotBelongAdException();
                } else {
                    if (auth.equals("ROLE_USER") && !optionalAdsComment.get().getSiteUserDetails().getSiteUser().getUsername().equals(username)) {
                        throw new NotAccessActionException();
                    } else {
                        Comment result = commentMapper.commentDtoToComment(commentDto, optionalAdsComment.get());
                        return commentMapper.commentToCommentDto(commentRepository.save(result));
                    }
                }
            }
        }
    }


//    Method for getting comments containing text
    @Override
    public ResponseWrapper<CommentDto> getCommentWithText(String text) {
        List<Comment> result = commentRepository.findCommentsByTextContains(text);
        if (result.isEmpty()) {
            throw new EmptyListException();
        } else {
            List<CommentDto> list = result.stream().map(commentMapper::commentToCommentDto).collect(Collectors.toList());
            ResponseWrapper<CommentDto> responseWrapperDto = new ResponseWrapper<>();
            responseWrapperDto.setResults(list);
            responseWrapperDto.setCount(list.size());
            return responseWrapperDto;
        }
    }


}
