package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.Comment;
import ru.skypro.homework.mappers.impl.CommentMapperImpl;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.repositories.CommentRepository;
import ru.skypro.homework.service.CommentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final AdsRepository adsRepository;
    private final CommentMapperImpl commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository,
                              AdsRepository adsRepository,
                              CommentMapperImpl commentMapper) {
        this.commentRepository = commentRepository;
        this.adsRepository = adsRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public ResponseWrapper<CommentDto> getListCommentDto(String idAds) {
        List<Comment> commentList = commentRepository.findAllByIdAuthor(Integer.parseInt(idAds));

        List<CommentDto> commentDtoList = commentList.stream()
                .map(commentMapper::commentToCommentDto)
                .collect(Collectors.toList());

        ResponseWrapper<CommentDto> commentDtoResponseWrapper = new ResponseWrapper<>();
        commentDtoResponseWrapper.setList(commentDtoList);
        commentDtoResponseWrapper.setCount(commentDtoList.size());
        return commentDtoResponseWrapper;
    }

    @Override
    public CommentDto addCommentDto(String idAds, CommentDto commentDto) {
        Ads ads = adsRepository.findAdsByPk(Integer.parseInt(idAds));
        if(ads != null){
            Comment comment = commentMapper.commentDtoToComment(commentDto);
            ads.addComment(comment);
            adsRepository.save(ads);
            commentRepository.save(comment);
            return commentDto;
        }
        return new CommentDto();
    }

    @Override
    public void deleteCommentDto(String idAds, Integer idComment) {
        Ads ads = adsRepository.findAdsByPk(Integer.parseInt(idAds));
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
        Ads ads = adsRepository.findAdsByPk(Integer.parseInt(idAds));
        Comment comment = commentRepository.findCommentById(idComment);
        if(ads != null && comment != null){
            return commentMapper.commentToCommentDto(comment);
        }
        return new CommentDto();
    }

    @Override
    public CommentDto updateCommentDto(String idAds, Integer idComment, CommentDto commentDto) {
        Ads ads = adsRepository.findAdsByPk(Integer.parseInt(idAds));
        Comment comment = commentRepository.findCommentById(idComment);
        if(ads != null && comment != null){
            Comment commentSave = commentMapper.commentDtoToComment(commentDto);
            ads.updateComment(commentSave);
            adsRepository.save(ads);
            commentRepository.save(commentSave);
            return commentDto;
        }
        return new CommentDto();
    }

    @Override
    public ResponseWrapper<CommentDto> getCommentWithText(String text) {
        List<Comment> result = commentRepository.findCommentsByTextContains(text);
        if (result.isEmpty()) {
            return null;
        } else {
            List<CommentDto> list = result.stream().map(commentMapper::commentToCommentDto).collect(Collectors.toList());
            ResponseWrapper<CommentDto> responseWrapperDto = new ResponseWrapper<>();
            responseWrapperDto.setList(list);
            responseWrapperDto.setCount(list.size());
            return responseWrapperDto;
        }
    }
}
