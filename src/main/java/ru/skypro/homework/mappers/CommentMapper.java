package ru.skypro.homework.mappers;

import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entities.Comment;

/**
 * Interface included methods for converting entity ads comment to dto and back again
 */
public interface CommentMapper {
    Comment commentDtoToComment(CommentDto commentDto, Comment comment);

    CommentDto commentToCommentDto(Comment comment);
}
