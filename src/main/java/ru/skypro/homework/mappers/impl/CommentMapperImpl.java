package ru.skypro.homework.mappers.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entities.Comment;
import ru.skypro.homework.mappers.CommentMapper;

@Service
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment commentDtoToComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getPk());
        comment.setText(comment.getText());
        comment.setIdAuthor(comment.getIdAuthor());
        comment.setCreatedAt(comment.getCreatedAt());
        return comment;
    }

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
