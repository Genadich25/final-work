package ru.skypro.homework.service;

import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.ResponseWrapper;

public interface CommentService {


    ResponseWrapper<CommentDto> getListCommentDto(String idAds);

    CommentDto addCommentDto(String idAds, CommentDto commentDto);

    void deleteCommentDto(String idAds, Integer idComment);

    CommentDto getCommentDto(String idAds, Integer idComment);

    public CommentDto updateCommentDto(String idAds, Integer idComment);

    ResponseWrapper<CommentDto> getCommentWithText(String text);


}
