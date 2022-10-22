package ru.skypro.homework.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.Comment;
import ru.skypro.homework.mappers.impl.CommentMapperImpl;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.repositories.AuthorityRepository;
import ru.skypro.homework.repositories.CommentRepository;
import ru.skypro.homework.service.CommentService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final AdsRepository adsRepository;
    private final CommentMapperImpl commentMapper;
    private final AuthorityRepository authorityRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              AdsRepository adsRepository,
                              CommentMapperImpl commentMapper, AuthorityRepository authorityRepository) {
        this.commentRepository = commentRepository;
        this.adsRepository = adsRepository;
        this.commentMapper = commentMapper;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public ResponseWrapper<CommentDto> getListCommentDto(String idAds) {
        List<Comment> commentList = commentRepository.findAllByIdAuthor(Integer.parseInt(idAds));

        List<CommentDto> commentDtoList = commentList.stream()
                .map(commentMapper::commentToCommentDto)
                .collect(Collectors.toList());

        ResponseWrapper<CommentDto> commentDtoResponseWrapper = new ResponseWrapper<>();
        commentDtoResponseWrapper.setResults(commentDtoList);
        commentDtoResponseWrapper.setCount(commentDtoList.size());
        return commentDtoResponseWrapper;
    }

    @Override
    public CommentDto addCommentDto(String idAds, CommentDto commentDto) {
        Optional<Ads> ads = adsRepository.findById(Integer.parseInt(idAds));
        if (ads.isPresent()) {
            Comment comment = commentMapper.commentDtoToComment(commentDto);
            ads.get().addComment(comment);
            adsRepository.save(ads.get());
            commentRepository.save(comment);
            return commentDto;
        } else {
            return null;
        }
    }

    @Override
    public String deleteCommentDto(String idAds, Integer idComment) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String auth = authorityRepository.findAuthorityByUsername(username).getAuthority();
        Optional<Ads> ads = adsRepository.findById(Integer.parseInt(idAds));
        if (ads.isEmpty()) {
            return null;
        }
        if (auth.equals("ROLE_USER") && !ads.get().getSiteUserDetails().getSiteUser().getUsername().equals(username)) {
            return "Not access";
        }
        Comment comment = commentRepository.findCommentById(idComment);
        if (comment != null) {
            Ads result = ads.get();
            result.removeComment(comment);
            adsRepository.save(result);
            commentRepository.delete(comment);
            return "SUCCESS";
        }
        throw new NotFoundException("Ads or Comment Not Found!");
    }

    @Override
    public CommentDto getCommentDto(String idAds, Integer idComment) {
        Ads ads = adsRepository.findAdsById(Integer.parseInt(idAds));
        Comment comment = commentRepository.findCommentById(idComment);
        if (ads != null && comment != null) {
            return commentMapper.commentToCommentDto(comment);
        } else {
            return null;
        }

    }

    @Override
    public CommentDto updateCommentDto(String idAds, Integer idComment, CommentDto commentDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String auth = authorityRepository.findAuthorityByUsername(username).getAuthority();
        Optional<Ads> ads = adsRepository.findById(Integer.parseInt(idAds));
        if (ads.isEmpty()) {
            return null;
        } else {
            if (auth.equals("ROLE_USER") && !ads.get().getSiteUserDetails().getSiteUser().getUsername().equals(username)) {
                CommentDto result = new CommentDto();
                result.setText("Not access");
                return result;
            } else {
                Optional<Comment> comment = commentRepository.findById(idComment);
                if (comment.isEmpty()) {
                    return null;
                } else {
                    if (!ads.get().getCommentList().contains(comment.get())) {
                        CommentDto bad = new CommentDto();
                        bad.setText("Bad");
                        return bad;
                    } else {
                        Comment commentSave = commentMapper.commentDtoToComment(commentDto);
                        ads.get().updateComment(commentSave);
                        adsRepository.save(ads.get());
                        commentRepository.save(commentSave);
                        return commentDto;
                    }
                }
            }
        }
    }

    @Override
    public ResponseWrapper<CommentDto> getCommentWithText(String text) {
        List<Comment> result = commentRepository.findCommentsByTextContains(text);
        if (result.isEmpty()) {
            return null;
        } else {
            List<CommentDto> list = result.stream().map(commentMapper::commentToCommentDto).collect(Collectors.toList());
            ResponseWrapper<CommentDto> responseWrapperDto = new ResponseWrapper<>();
            responseWrapperDto.setResults(list);
            responseWrapperDto.setCount(list.size());
            return responseWrapperDto;
        }
    }
}
