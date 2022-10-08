package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.Comment;
import ru.skypro.homework.mappers.CommentMapper;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.repositories.CommentRepository;
import ru.skypro.homework.service.CommentService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final AdsRepository adsRepository;

    public CommentServiceImpl(CommentRepository commentRepository, AdsRepository adsRepository) {
        this.commentRepository = commentRepository;
        this.adsRepository = adsRepository;
    }

    @Override
    public ResponseWrapper<CommentDto> getListCommentDto(String idAds) {
        List<Comment> commentList = commentRepository.findAllByIdAuthor(Integer.parseInt(idAds));

        List<CommentDto> commentDtoList = commentList.stream()
                .map(CommentMapper.INSTANCE::commentToCommentDto)
                .collect(Collectors.toList());

        ResponseWrapper<CommentDto> commentDtoResponseWrapper = new ResponseWrapper<>();
        commentDtoResponseWrapper.setList(commentDtoList);
        commentDtoResponseWrapper.setCount(commentDtoList.size());
        return commentDtoResponseWrapper;
    }

    @Override
    public CommentDto addCommentDto(String idAds, CommentDto commentDto) {
        Ads ads = adsRepository.findAdsById(Integer.parseInt(idAds));
        if(ads != null){
            Comment comment = CommentMapper.INSTANCE.commentDtoToComment(commentDto);
            ads.addComment(comment);
            adsRepository.save(ads);
            commentRepository.save(comment);
            return commentDto;
        }
        return new CommentDto();
    }

    @Override
    public void deleteCommentDto(String idAds, Integer idComment) {
        Ads ads = adsRepository.findAdsById(Integer.parseInt(idAds));
        Comment comment = commentRepository.findCommentById(idComment);
        if(ads != null && comment != null){
            ads.removeComment(comment);
            adsRepository.save(ads);
            commentRepository.delete(comment);
        }
        throw new NotFoundException("Ads or Comment Not Found!");
    }

    @Override
    public CommentDto getCommentDto(String idAds, Integer idComment) {
        Ads ads = adsRepository.findAdsById(Integer.parseInt(idAds));
        Comment comment = commentRepository.findCommentById(idComment);
        if(ads != null && comment != null){
            return CommentMapper.INSTANCE.commentToCommentDto(comment);
        }
        return new CommentDto();
    }

    @Override
    public CommentDto updateCommentDto(String idAds, Integer idComment) {
        Ads ads = adsRepository.findAdsById(Integer.parseInt(idAds));
        Comment comment = commentRepository.findCommentById(idComment);
        if(ads != null && comment != null){
            ads.updateComment(comment);
            adsRepository.save(ads);
            commentRepository.save(comment);
            return CommentMapper.INSTANCE.commentToCommentDto(comment);
        }
        return new CommentDto();
    }
}
