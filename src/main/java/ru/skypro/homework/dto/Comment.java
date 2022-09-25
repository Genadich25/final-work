package ru.skypro.homework.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer author;
    private LocalDateTime createdAt;
    private Integer pk;
    private String text;
}
