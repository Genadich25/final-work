package ru.skypro.homework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entities.Comment;

@Mapper
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "pk", target = "id")
    @Mapping(source = "author", target = "idAuthor")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "text", target = "text")
    Comment commentDtoToComment(CommentDto commentDto);

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "idAuthor", target = "author")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "text", target = "text")
    CommentDto commentToCommentDto(Comment comment);
}
