package ru.skypro.homework.service;

import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.ResponseWrapper;

/**
 * Interface including methods for working with entity comment
 */
public interface CommentService {

    /**
     * Method for getting all comments of one ad
     * @return ResponseWrapper<CommentDto>
     */
    ResponseWrapper<CommentDto> getListCommentDto(String idAds);

    /**
     * Method for creating comment for one ad
     * @return CommentDto
     */
    CommentDto addCommentDto(String idAds, String text);

    /**
     * Method remove one comment of ad by ad id and comment id
     * @return String
     */
    String deleteCommentDto(String idAds, Integer idComment);

    /**
     * Method for getting one comment of ad by ad id and comment id
     * @return CommentDto
     */
    CommentDto getCommentDto(String idAds, Integer idComment);

    /**
     * Method for updating comment
     * @return CommentDto
     */
    CommentDto updateCommentDto(String idAds, Integer idComment, CommentDto commentDto);

    /**
     * Method for getting comments containing text
     * @return ResponseWrapper
     */
    ResponseWrapper<CommentDto> getCommentWithText(String text);
}
