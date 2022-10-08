package ru.skypro.homework.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Integer pk;
    private Integer author;
    private LocalDateTime createdAt;
    private String text;
}
