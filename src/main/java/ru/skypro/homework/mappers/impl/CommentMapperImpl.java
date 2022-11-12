package ru.skypro.homework.mappers.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entities.Comment;
import ru.skypro.homework.mappers.CommentMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Class implements methods for converting ads comment entity to ads comment dto and back again
 */
@Service
public class CommentMapperImpl implements CommentMapper {

//    method converts from ads comment dto to ads comment entity
    @Override
    public Comment commentDtoToComment(CommentDto commentDto, Comment comment) {
        if (commentDto.getText() != null) {
            comment.setText(commentDto.getText());
            comment.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        }
        return comment;
    }

//    method converts from ads comment entity to ads comment dto
    @Override
    public CommentDto commentToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setPk(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthor(comment.getIdAuthor());
        commentDto.setCreatedAt(comment.getCreatedAt());
        return commentDto;
    }
}
